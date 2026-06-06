package teample;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundManager {
    private Clip bgmClip;
    private FloatControl bgmVolume;

    // 배경음악 재생 (반복 재생)
    public void playBgm(String fileName) {
        try {
            stopBgm(); // 기존 BGM 정지

            InputStream is = getClass().getResourceAsStream(fileName);
            if (is == null) {
                System.out.println("사운드 파일을 찾을 수 없음: " + fileName);
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);

            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);

            // 볼륨 조절 가능하면 살짝 줄임
            if (bgmClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                bgmVolume = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
                bgmVolume.setValue(-10.0f); // 데시벨 단위, 음수면 작아짐
            }

            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();

        } catch (Exception e) {
            System.out.println("BGM 재생 실패: " + e.getMessage());
        }
    }

    // 배경음악 정지
    public void stopBgm() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    // 한 번만 재생하는 효과음 (괴물 등장음, 유리 깨지는 소리 등)
    public void playSound(String fileName) {
        try {
            InputStream is = getClass().getResourceAsStream(fileName);
            if (is == null) {
                System.out.println("사운드 파일을 찾을 수 없음: " + fileName);
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (Exception e) {
            System.out.println("효과음 재생 실패: " + e.getMessage());
        }
    }

    // BGM 볼륨 조절 (-80.0 ~ 6.0 데시벨 범위, 0이 기본)
    public void setBgmVolume(float decibel) {
        if (bgmVolume != null) {
            bgmVolume.setValue(decibel);
        }
    }
}