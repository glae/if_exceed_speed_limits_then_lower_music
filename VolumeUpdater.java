package glae.speedlimitsmusic;

public class VolumeUpdater {

    public int evaluateNextVolume(int currentSpeed, int currentLimit) {

        int nextVolume = 0;

        nextVolume = decreaseVolumeWhenExcessLimit(currentSpeed, currentLimit, nextVolume);

        if (currentSpeed <= 70) {
            nextVolume += 7;
        } else if (currentSpeed <= 90) {
            nextVolume += 8;
        } else if (currentSpeed <= 110) {
            nextVolume += 9;
        } else {
            nextVolume += 10;
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
