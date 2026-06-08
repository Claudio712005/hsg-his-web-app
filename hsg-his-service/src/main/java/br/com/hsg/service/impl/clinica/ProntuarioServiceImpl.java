package br.com.hsg.service.impl.clinica;

import br.com.hsg.dao.AlergiaDAO;
import br.com.hsg.dao.ArquivoDAO;
import br.com.hsg.dao.ConsultaAnotacaoDAO;
import br.com.hsg.dao.ConsultaDAO;
import br.com.hsg.dao.PacienteDAO;
import br.com.hsg.dao.ReceitaDAO;
import br.com.hsg.domain.entity.Alergia;
import br.com.hsg.domain.entity.Arquivo;
import br.com.hsg.domain.entity.Consulta;
import br.com.hsg.domain.entity.ConsultaAnotacao;
import br.com.hsg.domain.entity.Paciente;
import br.com.hsg.domain.entity.Receita;
import br.com.hsg.domain.entity.ReceitaItem;
import br.com.hsg.domain.enums.IndicativoStatus;
import br.com.hsg.domain.enums.TipoResponsavel;
import br.com.hsg.service.dto.prontuario.AlergiaResumoDTO;
import br.com.hsg.service.dto.prontuario.AnexoResumoDTO;
import br.com.hsg.service.dto.prontuario.AnotacaoResumoDTO;
import br.com.hsg.service.dto.prontuario.ConsultaResumoDTO;
import br.com.hsg.service.dto.prontuario.PacienteResumoDTO;
import br.com.hsg.service.dto.prontuario.ProntuarioDTO;
import br.com.hsg.service.dto.prontuario.ReceitaResumoDTO;
import br.com.hsg.service.facade.clinica.ProntuarioServiceFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class ProntuarioServiceImpl implements ProntuarioServiceFacade {

    @EJB private PacienteDAO          pacienteDAO;
    @EJB private ConsultaDAO          consultaDAO;
    @EJB private ConsultaAnotacaoDAO  anotacaoDAO;
    @EJB private ArquivoDAO           arquivoDAO;
    @EJB private ReceitaDAO           receitaDAO;
    @EJB private AlergiaDAO           alergiaDAO;

    @Override
    public ProntuarioDTO montarParaPaciente(Long idPaciente, Long idSolicitante,
                                              TipoResponsavel tipoSolicitante) {
        if (idPaciente == null) {
            throw new IllegalArgumentException("Paciente é obrigatório.");
        }
        autorizar(idPaciente, idSolicitante, tipoSolicitante);

        Paciente p = pacienteDAO.buscarPorId(idPaciente);
        if (p == null) {
            throw new IllegalArgumentException("Paciente não encontrado.");
        }

        ProntuarioDTO dto = new ProntuarioDTO();
        dto.setPaciente(montarResumoPaciente(p));
        dto.setAlergias(montarAlergias(p.getId()));
        dto.setConsultas(montarConsultas(p.getId()));
        return dto;
    }

    @Override
    public List<PacienteBuscaDTO> buscarPacientes(String termo, Long idSolicitante,
                                                    TipoResponsavel tipoSolicitante, int limite) {
        if (tipoSolicitante == null
                || tipoSolicitante == TipoResponsavel.PACIENTE
                || tipoSolicitante == TipoResponsavel.SISTEMA) {
            throw new IllegalStateException("Sem permissão para buscar pacientes.");
        }
        if (termo == null || termo.trim().length() < 2) {
            return Collections.emptyList();
        }
        List<Paciente> pacientes = pacienteDAO.buscarPorTermo(termo, limite);
        List<PacienteBuscaDTO> out = new ArrayList<>(pacientes.size());
        for (Paciente p : pacientes) {
            out.add(new PacienteBuscaDTO(p.getId(), p.getNomeCompleto()));
        }
        return out;
    }

    private void autorizar(Long idPaciente, Long idSolicitante, TipoResponsavel tpSolic) {
        if (tpSolic == null) {
            throw new IllegalStateException("Sem permissão para acessar o prontuário.");
        }
        switch (tpSolic) {
            case ADMIN:
            case ENFERMEIRO:
            case MEDICO:
                return;
            case PACIENTE:
                if (idSolicitante == null || !idSolicitante.equals(idPaciente)) {
                    throw new IllegalStateException("Pacientes só acessam o próprio prontuário.");
                }
                return;
            default:
                throw new IllegalStateException("Tipo de solicitante não suportado.");
        }
    }

    private PacienteResumoDTO montarResumoPaciente(Paciente p) {
        PacienteResumoDTO r = new PacienteResumoDTO();
        r.setId(p.getId());
        r.setNomeCompleto(p.getNomeCompleto());
        r.setDataNascimento(p.getDataNascimento());
        if (p.getDataNascimento() != null) {
            r.setIdade(Period.between(p.getDataNascimento(), LocalDate.now()).getYears());
        }
        r.setEmail(p.getEmail());
        r.setTelefone(p.getTelefone());
        r.setCpfMascarado("***.***.***-**");
        return r;
    }

    private List<AlergiaResumoDTO> montarAlergias(Long idPaciente) {
        List<Alergia> all = alergiaDAO.listarPorPaciente(idPaciente, 0, 200);
        List<AlergiaResumoDTO> out = new ArrayList<>();
        for (Alergia a : all) {
            if (a.getStatusAlergia() != null
                    && "EXCLUIDA".equalsIgnoreCase(a.getStatusAlergia().name())) continue;
            AlergiaResumoDTO dto = new AlergiaResumoDTO();
            dto.setId(a.getId());
            dto.setNome(a.getNome());
            dto.setTipo(a.getTipoAlergia() != null ? a.getTipoAlergia().name() : null);
            dto.setGravidade(a.getGravidadeAlergia() != null ? a.getGravidadeAlergia().name() : null);
            dto.setStatus(a.getStatusAlergia() != null ? a.getStatusAlergia().name() : null);
            dto.setReacao(a.getReacao());
            out.add(dto);
        }
        return out;
    }

    private List<ConsultaResumoDTO> montarConsultas(Long idPaciente) {
        List<Consulta> consultas = consultaDAO.listarPorPaciente(idPaciente);
        List<ConsultaResumoDTO> out = new ArrayList<>(consultas.size());
        for (Consulta c : consultas) {
            ConsultaResumoDTO dto = new ConsultaResumoDTO();
            dto.setId(c.getId());
            dto.setDataConsulta(c.getDataConsulta());
            dto.setStatus(c.getStatus() != null ? c.getStatus().name() : null);
            dto.setTipoAtendimento(c.getTipoAtendimento() != null ? c.getTipoAtendimento().name() : null);
            dto.setObservacaoClinica(c.getObservacaoClinica());
            dto.setMotivoCancelamento(c.getMotivoCancelamento());
            if (c.getMedico() != null) {
                dto.setMedicoNome(c.getMedico().getNomeCompleto());
                if (c.getMedico().getCrm() != null) {
                    dto.setMedicoCrm(c.getMedico().getCrm().getFormatado());
                }
                if (c.getMedico().getEspecialidade() != null) {
                    dto.setEspecialidade(c.getMedico().getEspecialidade().getNome());
                }
            }
            dto.setAnotacoes(montarAnotacoes(c.getId()));
            dto.setAnexos(montarAnexos(c.getId()));
            popularReceitas(dto, c.getId());
            out.add(dto);
        }
        return out;
    }

    private List<AnotacaoResumoDTO> montarAnotacoes(Long idConsulta) {
        List<ConsultaAnotacao> anots = anotacaoDAO.listarPorConsulta(idConsulta);
        List<AnotacaoResumoDTO> out = new ArrayList<>(anots.size());
        for (ConsultaAnotacao a : anots) {
            AnotacaoResumoDTO dto = new AnotacaoResumoDTO();
            dto.setId(a.getId());
            dto.setTitulo(a.getTitulo());
            dto.setDescricao(a.getDescricao());
            dto.setDataCriacao(a.getDataCriacao());
            dto.setResponsavelTipo(a.getTipoResponsavel() != null ? a.getTipoResponsavel().name() : null);
            out.add(dto);
        }
        return out;
    }

    private List<AnexoResumoDTO> montarAnexos(Long idConsulta) {
        List<Arquivo> anexos = arquivoDAO.listarPorConsulta(idConsulta);
        List<AnexoResumoDTO> out = new ArrayList<>(anexos.size());
        for (Arquivo a : anexos) {
            if (a.getStatus() != IndicativoStatus.A) continue;
            AnexoResumoDTO dto = new AnexoResumoDTO();
            dto.setId(a.getId());
            dto.setNomeOriginal(a.getNomeOriginal());
            dto.setContentType(a.getContentType());
            dto.setTamanhoBytes(a.getTamanhoBytes());
            dto.setDominio(a.getDominio() != null ? a.getDominio().name() : null);
            dto.setDataUpload(a.getDataUpload());
            dto.setResponsavelTipo(a.getTipoResponsavel() != null ? a.getTipoResponsavel().name() : null);
            out.add(dto);
        }
        return out;
    }

    private void popularReceitas(ConsultaResumoDTO dto, Long idConsulta) {
        List<Receita> todas = receitaDAO.listarTodasPorConsulta(idConsulta);
        for (Receita r : todas) {
            ReceitaResumoDTO rd = new ReceitaResumoDTO();
            rd.setId(r.getId());
            rd.setDataEmissao(r.getDataEmissao());
            rd.setAtiva(r.getStatus() == IndicativoStatus.A);
            for (ReceitaItem it : r.getItens()) {
                ReceitaResumoDTO.ItemDTO id = new ReceitaResumoDTO.ItemDTO();
                id.setMedicamento(it.getMedicamento());
                id.setPosologia(it.getPosologia());
                id.setObservacao(it.getObservacao());
                id.setCid10(it.getCid10());
                rd.getItens().add(id);
            }
            if (rd.isAtiva()) {
                dto.setReceitaAtiva(rd);
            } else {
                dto.getReceitasInativas().add(rd);
            }
        }
    }
}
