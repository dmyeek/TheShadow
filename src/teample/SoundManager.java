package teample;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.*;

public class SoundManager {
    private Clip bgmClip;

    /**
     * 배경음악(BGM)을 로드하고 무한 반복 재생하는 메서드
     * @param fileName src/teample/ 폴더 내의 사운드 파일 이름 (확장자 .wav 필수)
     */
    public void playBgm(String fileName) {
        // 기존에 재생 중이던 BGM이 있다면 먼저 정지하고 메모리 해제
        stopBgm();

        try {
            // 리소스 폴더(src/teample)에서 안전하게 파일을 읽어오기 위한 스트림 세팅
            InputStream is = getClass().getResourceAsStream(fileName);
            if (is == null) {
                System.out.println("사운드 파일을 찾을 수 없습니다: " + fileName);
                return;
            }
            
            // Clip 버그 방지를 위해 Buffer 스트림으로 감싸기
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
            
            // 오디오 클립 생성 및 오픈
            bgmClip = AudioSystem.getClip();
            bgmClip.open(ais);
            
            // 호러 게임 필수 설정: 무한 반복 재생 구동
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
            
        } catch (Exception e) {
            System.out.println("BGM 재생 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 재생 중인 배경음악을 멈추는 메서드 (방이 바뀌거나 배틀이 시작될 때 사용)
     */
    public void stopBgm() {
        if (bgmClip != null) {
            if (bgmClip.isRunning()) {
                bgmClip.stop();
            }
            bgmClip.close(); // 사용이 끝난 클립의 메모리 자원 반환
            bgmClip = null;
        }
    }
}