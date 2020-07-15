package kr.ac.green;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public final class PlayListScrollBarUI extends BasicScrollBarUI {
    /*
     * (non-Javadoc)
     *  ScrollBar 를 구성할때의 색상을 지정, thumbRect와 trackRect 과 같은 필드는 api 를 참조하면 그림과 함께 어느것인지
     * 자세하게 나온다.
     * @see javax.swing.plaf.basic.BasicScrollBarUI#configureScrollBarColors()
     */
    protected void configureScrollBarColors() {
        thumbRect.width = 5;
        trackRect.width = 5;
       
        thumbColor = new Color(0x1E90FF); // 스크롤 바 네모 축
        thumbDarkShadowColor = Color.BLUE;
        thumbHighlightColor = Color.GREEN;
        thumbLightShadowColor = Color.ORANGE;
        trackColor = new Color(0xEBFBFF); // 스크롤 바 라인 
        trackHighlightColor = new Color(0xE0EBFF); // 선택시 표시 부분
       
//        AWTUtilitiesWrapper.setComponentShape((Window)thumbRect, shape);
    }

    /*
     * (non-Javadoc)
     *  ScrollBar 상단에 화살표 모양의 버튼 색상 지정
     * @see javax.swing.plaf.basic.BasicScrollBarUI#createDecreaseButton(int)
     */
    protected JButton createDecreaseButton(int orientation) {
        JButton button = new BasicArrowButton(orientation);
        button.setBackground(new Color(0x49BFFF));
        button.setForeground(new Color(0x49BFFF));
        return button;
    }

    /*
     * (non-Javadoc)
     *  ScrollBar 하단에 화살표 모양의 버튼 색상 지정
     * @see javax.swing.plaf.basic.BasicScrollBarUI#createIncreaseButton(int)
     */
    protected JButton createIncreaseButton(int orientation) {
        JButton button = new BasicArrowButton(orientation);
        button.setBackground(new Color(0x49BFFF));
        button.setForeground(new Color(0x49BFFF));
        return button;
    }
    @Override
    protected Dimension getMaximumThumbSize() {
        // TODO Auto-generated method stub
        return new Dimension(10, 20);
    }
}