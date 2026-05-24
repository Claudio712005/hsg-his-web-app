package br.com.hsg.service.facade.admin;

import br.com.hsg.domain.entity.AgendaMedica;
import br.com.hsg.domain.entity.AgendaMedicaExcecao;
import br.com.hsg.domain.entity.AgendaMedicaSlot;
import br.com.hsg.domain.entity.Medico;
import br.com.hsg.domain.entity.MedicoEspecialidade;
import br.com.hsg.domain.enums.DiaSemana;
import br.com.hsg.domain.enums.TipoExcecaoAgenda;

import javax.ejb.Local;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Local
public interface AgendaMedicaServiceFacade {

    List<Medico> listarMedicosAtivos();

    List<MedicoEspecialidade> listarEspecialidadesDoMedico(Long idMedico);

    List<AgendaMedica> listarGradesPorMedico(Long idMedico);
    AgendaMedica criarGrade(Long idMedico, DiaSemana diaSemana, LocalTime horaInicio,
                             LocalTime horaFim, Integer duracaoMinutos);
    AgendaMedica atualizarGrade(Long idGrade, LocalTime horaInicio, LocalTime horaFim,
                                 Integer duracaoMinutos);
    void ativarGrade(Long idGrade);
    void inativarGrade(Long idGrade);

    List<AgendaMedicaExcecao> listarExcecoesPorMedico(Long idMedico);
    AgendaMedicaExcecao criarExcecao(Long idMedico, LocalDateTime inicio, LocalDateTime fim,
                                      TipoExcecaoAgenda tipo, String motivo);
    void removerExcecao(Long idExcecao);

    List<AgendaMedicaSlot> listarSlotsPorMedicoPeriodo(Long idMedico,
                                                        LocalDateTime inicio,
                                                        LocalDateTime fim);
    long contarSlotsLivresFuturos(Long idMedico);

    int gerarSlots(Long idMedico, int diasFrente);
}
