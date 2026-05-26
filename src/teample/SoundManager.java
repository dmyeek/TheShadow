package teample;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundManager {
    private Clip bgmClip;
    // --- [추가] 기본 볼륨 설정 변수 (데시벨 단위) ---
    //  0.0f = 원래 음량 (최대)
    // -10.0f = 대략 반으로 줄어듦
    // -20.0f = 잔잔하게 배경음으로 깔리는 크기
    private float currentVolume = -15.0f; 

    // 배경음악 재생 메서드
    public void playBgm(String fileName) {
        try {
            InputStream is = getClass().getResourceAsStream("/teample/" + fileName);
            if (is == null) {
                System.out.println("사운드 파일을 찾을 수 없습니다: " + fileName);
                return;
            }
            
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
            
            bgmClip = AudioSystem.getClip();
            bgmClip.open(ais);
            
            // --- [추가] 음악이 시작되기 전에 저장된 볼륨 값 적용 ---
            setVolume(currentVolume);
            
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- [신규 추가] 실시간으로 음량을 조절하는 메서드 ---
    public void setVolume(float volume) {
        this.currentVolume = volume;
        
        // 데시벨 한계값 보정 (자바 Clip은 보통 -80dB 이하로 내려가면 무음 처리됨)
        if (currentVolume < -80.0f) currentVolume = -80.0f;
        if (currentVolume > 6.0f) currentVolume = 6.0f; // 최대치 제한
        
        if (bgmClip != null && bgmClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) bgmClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(currentVolume); // 실제 오디오 라인에 데시벨 값 주입
        }
    }

    public void stopBgm() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }
}