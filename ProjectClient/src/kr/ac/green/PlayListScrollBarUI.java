package kr.ac.green;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public final class PlayListScrollBarUI extends BasicScrollBarUI {
    /*
     * (non-Javadoc)
     *  ScrollBar �� �����Ҷ��� ������ ����, thumbRect�� trackRect �� ���� �ʵ�� api �� �����ϸ� �׸��� �Բ� ���������
     * �ڼ��ϰ� ���´�.
     * @see javax.swing.plaf.basic.BasicScrollBarUI#configureScrollBarColors()
     */
    protected void configureScrollBarColors() {
        thumbRect.width = 5;
        trackRect.width = 5;
       
        thumbColor = new Color(0x1E90FF); // ��ũ�� �� �׸� ��
        thumbDarkShadowColor = Color.BLUE;
        thumbHighlightColor = Color.GREEN;
        thumbLightShadowColor = Color.ORANGE;
        trackColor = new Color(0xEBFBFF); // ��ũ�� �� ���� 
        trackHighlightColor = new Color(0xE0EBFF); // ���ý� ǥ�� �κ�
       
//        AWTUtilitiesWrapper.setComponentShape((Window)thumbRect, shape);
    }

    /*
     * (non-Javadoc)
     *  ScrollBar ��ܿ� ȭ��ǥ ����� ��ư ���� ����
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
     *  ScrollBar �ϴܿ� ȭ��ǥ ����� ��ư ���� ����
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