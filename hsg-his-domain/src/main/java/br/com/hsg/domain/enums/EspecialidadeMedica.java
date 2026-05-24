package br.com.hsg.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum EspecialidadeMedica {

    ACUPUNTURA("Acupuntura"),
    ALERGIA_IMUNOLOGIA("Alergia e Imunologia"),
    ANESTESIOLOGIA("Anestesiologia"),
    ANGIOLOGIA("Angiologia"),
    CANCEROLOGIA("Cancerologia / Oncologia"),
    CARDIOLOGIA("Cardiologia"),
    CIRURGIA_CARDIOVASCULAR("Cirurgia Cardiovascular"),
    CIRURGIA_CABECA_PESCOCO("Cirurgia de Cabeça e Pescoço"),
    CIRURGIA_DIGESTIVA("Cirurgia do Aparelho Digestivo"),
    CIRURGIA_GERAL("Cirurgia Geral"),
    CIRURGIA_MANO("Cirurgia da Mão"),
    CIRURGIA_PLASTICA("Cirurgia Plástica"),
    CIRURGIA_TORACICA("Cirurgia Torácica"),
    CIRURGIA_VASCULAR("Cirurgia Vascular"),
    CLINICA_MEDICA("Clínica Médica"),
    COLOPROCTOLOGIA("Coloproctologia"),
    DERMATOLOGIA("Dermatologia"),
    ENDOCRINOLOGIA("Endocrinologia e Metabologia"),
    ENDOSCOPIA("Endoscopia"),
    GASTROENTEROLOGIA("Gastroenterologia"),
    GENETICA_MEDICA("Genética Médica"),
    GERIATRIA("Geriatria"),
    GINECOLOGIA_OBSTETRICIA("Ginecologia e Obstetrícia"),
    HEMATOLOGIA("Hematologia e Hemoterapia"),
    HEPATOLOGIA("Hepatologia"),
    INFECTOLOGIA("Infectologia"),
    MASTOLOGIA("Mastologia"),
    MEDICINA_EMERGENCIA("Medicina de Emergência"),
    MEDICINA_FAMILIA("Medicina de Família e Comunidade"),
    MEDICINA_INTENSIVA("Medicina Intensiva"),
    MEDICINA_LEGAL("Medicina Legal e Perícia Médica"),
    MEDICINA_NUCLEAR("Medicina Nuclear"),
    MEDICINA_PREVENTIVA("Medicina Preventiva e Social"),
    MEDICINA_TRABALHO("Medicina do Trabalho"),
    MEDICINA_ESPORTIVA("Medicina do Esporte"),
    NEFROLOGIA("Nefrologia"),
    NEUROCIRURGIA("Neurocirurgia"),
    NEUROLOGIA("Neurologia"),
    NUTROLOGIA("Nutrologia"),
    OFTALMOLOGIA("Oftalmologia"),
    ORTOPEDIA("Ortopedia e Traumatologia"),
    OTORRINOLARINGOLOGIA("Otorrinolaringologia"),
    PATOLOGIA("Patologia"),
    PEDIATRIA("Pediatria"),
    PNEUMOLOGIA("Pneumologia"),
    PSIQUIATRIA("Psiquiatria"),
    RADIOLOGIA("Radiologia e Diagnóstico por Imagem"),
    RADIOTERAPIA("Radioterapia"),
    REUMATOLOGIA("Reumatologia"),
    UROLOGIA("Urologia");

    private String descricao;

    public static EspecialidadeMedica fromDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) return null;
        for (EspecialidadeMedica e : values()) {
            if (e.descricao.equalsIgnoreCase(descricao.trim())) {
                return e;
            }
        }
        throw new IllegalArgumentException("Especialidade médica desconhecida: " + descricao);
    }
}
