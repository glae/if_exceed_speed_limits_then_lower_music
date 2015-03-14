package glae.speedlimitsmusic;

public class VolumeUpdater {

    public int evaluateNextVolume(int currentSpeed, int currentLimit) {

        int nextVolume = 0;

        nextVolume = decreaseVolumeWhenExcessLimit(currentSpeed, currentLimit, nextVolume);
        nextVolume = adjustVolumeAccordingToVehicleSpeed(currentSpeed, nextVolume);

        return nextVolume;
    }

    private int adjustVolumeAccordingToVehicleSpeed(int currentSpeed, int nextVolume) {
        if (currentSpeed <= 30) {
            nextVolume += 7;
        } else if (currentSpeed <= 105) {
            nextVolume += 8;
        } else {
            nextVolume += 9;
        }
        return nextVolume;
    }

    private int decreaseVolumeWhenExcessLimit(int currentSpeed, int currentLimit, int nextVolume) {
        if (currentSpeed >= currentLimit) {
            if (currentSpeed >= currentLimit + 15) {
                nextVolume -= 3;
            } else if (currentSpeed >= currentLimit + 10) {
                nextVolume -= 2;
            } else if (currentSpeed >= currentLimit + 5) {
                nextVolume--;
            }
        }
        return nextVolume;
    }
}
