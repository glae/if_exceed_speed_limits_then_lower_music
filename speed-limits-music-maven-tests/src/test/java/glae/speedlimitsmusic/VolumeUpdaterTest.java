package glae.speedlimitsmusic;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VolumeUpdaterTest {

    private static final int ANY_LIMIT = 999;

    VolumeUpdater updater = new VolumeUpdater();

    @Test
    public void when_I_drive_between_0_and_30_kmph_I_want_the_volume_to_be_7() {
        int currentSpeed = 0;
        int nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isEqualTo(7);

        currentSpeed = 31;
        nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isNotEqualTo(7);
    }

    @Test
    public void when_I_drive_between_31_and_105_kmph_I_want_the_volume_to_be_8() {
        int currentSpeed = 31;
        int nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isEqualTo(8);

        currentSpeed = 106;
        nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isNotEqualTo(8);
    }

    @Test
    public void when_I_drive_between_106_and_plus_I_want_the_volume_to_be_9() {
        int currentSpeed = 106;
        int nextVolume = updater.evaluateNextVolume(currentSpeed, ANY_LIMIT);
        assertThat(nextVolume).isEqualTo(9);
    }


    @Test
    public void when_I_drive_5_kmph_over_the_limit_then_decrease_volume_by_1_unit() {

        int currentSpeed = 55;
        int currentLimit = 50;

        int nextVolume = updater.evaluateNextVolume(currentSpeed, currentLimit);
        assertThat(nextVolume).isEqualTo(7);
    }

    @Test
    public void when_I_drive_5_kmph_over_the_limit_then_decrease_volume_by_2_units() {

        int currentSpeed = 60;
        int currentLimit = 50;

        int nextVolume = updater.evaluateNextVolume(currentSpeed, currentLimit);
        assertThat(nextVolume).isEqualTo(6);
    }

    @Test
    public void when_I_drive_15_kmph_over_the_limit_then_decrease_volume_by_3_units() {

        int currentSpeed = 65;
        int currentLimit = 50;

        int nextVolume = updater.evaluateNextVolume(currentSpeed, currentLimit);
        assertThat(nextVolume).isEqualTo(5);
    }


    @Test
    public void when_I_drive_at_101_instead_of_90_kmph() {

        int currentSpeed = 101;
        int currentLimit = 90;

        int nextVolume = updater.evaluateNextVolume(currentSpeed, currentLimit);
        assertThat(nextVolume).isEqualTo(6);
    }

}
