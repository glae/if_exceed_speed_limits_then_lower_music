package glae.speedlimitsmusic;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VolumeUpdaterTest {

    private static final int ANY_LIMIT = 999;

    VolumeUpdater updater = new VolumeUpdater();

    @Test
    public void quand_je_roule_entre_0_et_70kmh_je_veux_que_le_son_soit_de_7() {
        int currentSpeed = 0;
        int nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isEqualTo(7);

        currentSpeed = 71;
        nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isNotEqualTo(7);
    }

    @Test
    public void quand_je_roule_entre_71_et_90kmh_je_veux_que_le_son_soit_de_8() {
        int currentSpeed = 71;
        int nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isEqualTo(8);

        currentSpeed = 91;
        nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isNotEqualTo(8);
    }

    @Test
    public void quand_je_roule_entre_91_et_110kmh_je_veux_que_le_son_soit_de_9() {
        int currentSpeed = 91;
        int nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isEqualTo(9);

        currentSpeed = 111;
        nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isNotEqualTo(9);
    }

    @Test
    public void quand_je_roule_entre_111_et_plus_je_veux_que_le_son_soit_de_10() {
        int currentSpeed = 111;
        int nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isEqualTo(10);
    }


    @Test
    public void quand_je_roule_5_km_h_au_delà_de_la_limite_baisser_le_volume_de_1_cran() {

        int currentSpeed = 55;
        int currentLimit = 50;

        int nextVolume = updater.evaluateNextVolume(currentSpeed, currentLimit);
        assertThat(nextVolume).isEqualTo(6);
    }

    @Test
    public void quand_je_roule_5_km_h_au_delà_de_la_limite_baisser_le_volume_de_2_crans() {

        int currentSpeed = 60;
        int currentLimit = 50;

        int nextVolume = updater.evaluateNextVolume(currentSpeed, currentLimit);
        assertThat(nextVolume).isEqualTo(5);
    }

    @Test
    public void quand_je_roule_15_km_h_au_delà_de_la_limite_baisser_le_volume_de_3_crans() {

        int currentSpeed = 65;
        int currentLimit = 50;

        int nextVolume = updater.evaluateNextVolume(currentSpeed, currentLimit);
        assertThat(nextVolume).isEqualTo(4);
    }


    @Test
    public void quand_je_roule_à_101_au_lieu_de_90() {

        int currentSpeed = 101;
        int currentLimit = 90;

        int nextVolume = updater.evaluateNextVolume(currentSpeed, currentLimit);
        assertThat(nextVolume).isEqualTo(7);
    }

}
