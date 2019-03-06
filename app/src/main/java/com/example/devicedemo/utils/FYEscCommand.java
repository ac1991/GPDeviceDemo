//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.devicedemo.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import com.tools.command.GpUtils;
import com.tools.command.LabelCommand.FOOT;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FYEscCommand {
    private static final String DEBUG_TAG = "FYEscCommand";
    Vector<Byte> Command = null;

    public FYEscCommand() {
        this.Command = new Vector(4096, 1024);
    }

    private void addArrayToCommand(byte[] array) {
        for(int i = 0; i < array.length; ++i) {
            this.Command.add(array[i]);
        }

    }

    private void addStrToCommand(String str) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            } catch (UnsupportedEncodingException var4) {
                var4.printStackTrace();
            }

            for(int i = 0; i < bs.length; ++i) {
                this.Command.add(bs[i]);
            }
        }

    }

    private void addStrToCommand(String str, String charset) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            } catch (UnsupportedEncodingException var5) {
                var5.printStackTrace();
            }

            for(int i = 0; i < bs.length; ++i) {
                this.Command.add(bs[i]);
            }
        }

    }

    private void addStrToCommandUTF8Encoding(String str, int length) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var5) {
                var5.printStackTrace();
            }

            Log.d("EscCommand", "bs.length" + bs.length);
            if (length > bs.length) {
                length = bs.length;
            }

            Log.d("EscCommand", "length" + length);

            for(int i = 0; i < length; ++i) {
                this.Command.add(bs[i]);
            }
        }

    }

    private void addStrToCommand(String str, int length) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            } catch (UnsupportedEncodingException var5) {
                var5.printStackTrace();
            }

            Log.d("EscCommand", "bs.length" + bs.length);
            if (length > bs.length) {
                length = bs.length;
            }

            Log.d("EscCommand", "length" + length);

            for(int i = 0; i < length; ++i) {
                this.Command.add(bs[i]);
            }
        }

    }

    public void addHorTab() {
        byte[] command = new byte[]{9};
        this.addArrayToCommand(command);
    }

    public void addText(String text) {
        this.addStrToCommand(text);
    }

    public void addText(String text, String charsetName) {
        this.addStrToCommand(text, charsetName);
    }

    public void addArabicText(String text) {
        text = FYGpUtils.reverseLetterAndNumber(text);
        text = FYGpUtils.splitArabic(text);
        String[] fooInput = text.split("\\n");
        String[] var6 = fooInput;
        int var5 = fooInput.length;

        for(int var4 = 0; var4 < var5; ++var4) {
            String in = var6[var4];
            byte[] output = FYGpUtils.string2Cp864(in);

            for(int i = 0; i < output.length; ++i) {
                if (output[i] == -16) {
                    this.addArrayToCommand(new byte[]{27, 116, 29, -124, 27, 116, 22});
                } else if (output[i] == 127) {
                    this.Command.add((byte) -41);
                } else {
                    this.Command.add(output[i]);
                }
            }
        }

    }

    public void addPrintAndLineFeed() {
        byte[] command = new byte[]{10};
        this.addArrayToCommand(command);
    }

    public void RealtimeStatusTransmission(FYEscCommand.STATUS status) {
        byte[] command = new byte[]{16, 4, status.getValue()};
        this.addArrayToCommand(command);
    }

    public void addGeneratePluseAtRealtime(FOOT foot, byte t) {
        byte[] command = new byte[]{16, 20, 1, (byte)foot.getValue(), 0};
        if (t > 8) {
            t = 8;
        }

        command[4] = t;
        this.addArrayToCommand(command);
    }

    public void addSound(byte n, byte t) {
        byte[] command = new byte[]{27, 66, 0, 0};
        if (n < 0) {
            n = 1;
        } else if (n > 9) {
            n = 9;
        }

        if (t < 0) {
            t = 1;
        } else if (t > 9) {
            t = 9;
        }

        command[2] = n;
        command[3] = t;
        this.addArrayToCommand(command);
    }

    public void addSetRightSideCharacterSpacing(byte n) {
        byte[] command = new byte[]{27, 32, n};
        this.addArrayToCommand(command);
    }

    public Vector<Byte> getCommand() {
        return this.Command;
    }

    public void addSelectPrintModes(FYEscCommand.FONT font, FYEscCommand.ENABLE emphasized, FYEscCommand.ENABLE doubleheight, FYEscCommand.ENABLE doublewidth, FYEscCommand.ENABLE underline) {
        byte temp = 0;
        if (font == FYEscCommand.FONT.FONTB) {
            temp = 1;
        }

        if (emphasized == FYEscCommand.ENABLE.ON) {
            temp = (byte)(temp | 8);
        }

        if (doubleheight == FYEscCommand.ENABLE.ON) {
            temp = (byte)(temp | 16);
        }

        if (doublewidth == FYEscCommand.ENABLE.ON) {
            temp = (byte)(temp | 32);
        }

        if (underline == FYEscCommand.ENABLE.ON) {
            temp = (byte)(temp | 128);
        }

        byte[] command = new byte[]{27, 33, temp};
        this.addArrayToCommand(command);
    }

    public void addSetAbsolutePrintPosition(short n) {
        byte[] command = new byte[]{27, 36, 0, 0};
        byte nl = (byte)(n % 256);
        byte nh = (byte)(n / 256);
        command[2] = nl;
        command[3] = nh;
        this.addArrayToCommand(command);
    }

    public void addSelectOrCancelUserDefineCharacter(FYEscCommand.ENABLE enable) {
        byte[] command = new byte[]{27, 37, 0};
        if (enable == FYEscCommand.ENABLE.ON) {
            command[2] = 1;
        } else {
            command[2] = 0;
        }

        this.addArrayToCommand(command);
    }

    public void addTurnUnderlineModeOnOrOff(FYEscCommand.UNDERLINE_MODE underline) {
        byte[] command = new byte[]{27, 45, underline.getValue()};
        this.addArrayToCommand(command);
    }

    public void addSelectDefualtLineSpacing() {
        byte[] command = new byte[]{27, 50};
        this.addArrayToCommand(command);
    }

    public void addSetLineSpacing(byte n) {
        byte[] command = new byte[]{27, 51, n};
        this.addArrayToCommand(command);
    }

    public void addCancelUserDefinedCharacters(byte n) {
        byte[] command = new byte[]{27, 63, 0};
        if (n >= 32 && n <= 126) {
            command[2] = n;
        } else {
            command[2] = 32;
        }

        this.addArrayToCommand(command);
    }

    public void addInitializePrinter() {
        byte[] command = new byte[]{27, 64};
        this.addArrayToCommand(command);
    }

    public void addTurnEmphasizedModeOnOrOff(FYEscCommand.ENABLE enabel) {
        byte[] command = new byte[]{27, 69, enabel.getValue()};
        this.addArrayToCommand(command);
    }

    public void addTurnDoubleStrikeOnOrOff(FYEscCommand.ENABLE enabel) {
        byte[] command = new byte[]{27, 71, enabel.getValue()};
        this.addArrayToCommand(command);
    }

    public void addPrintAndFeedPaper(byte n) {
        byte[] command = new byte[]{27, 74, n};
        this.addArrayToCommand(command);
    }

    public void addSelectCharacterFont(FYEscCommand.FONT font) {
        byte[] command = new byte[]{27, 77, font.getValue()};
        this.addArrayToCommand(command);
    }

    public void addSelectInternationalCharacterSet(FYEscCommand.CHARACTER_SET set) {
        byte[] command = new byte[]{27, 82, set.getValue()};
        this.addArrayToCommand(command);
    }

    public void addTurn90ClockWiseRotatin(FYEscCommand.ENABLE enabel) {
        byte[] command = new byte[]{27, 86, enabel.getValue()};
        this.addArrayToCommand(command);
    }

    public void addSetRelativePrintPositon(short n) {
        byte[] command = new byte[]{27, 92, 0, 0};
        byte nl = (byte)(n % 256);
        byte nh = (byte)(n / 256);
        command[2] = nl;
        command[3] = nh;
        this.addArrayToCommand(command);
    }

    public void addSelectJustification(FYEscCommand.JUSTIFICATION just) {
        byte[] command = new byte[]{27, 97, just.getValue()};
        this.addArrayToCommand(command);
    }

    public void addPrintAndFeedLines(byte n) {
        byte[] command = new byte[]{27, 100, n};
        this.addArrayToCommand(command);
    }

    public void addGeneratePlus(FOOT foot, byte t1, byte t2) {
        byte[] command = new byte[]{27, 112, (byte)foot.getValue(), t1, t2};
        this.addArrayToCommand(command);
    }

    public void addSelectCodePage(FYEscCommand.CODEPAGE page) {
        byte[] command = new byte[]{27, 116, page.getValue()};
        this.addArrayToCommand(command);
    }

    public void addTurnUpsideDownModeOnOrOff(FYEscCommand.ENABLE enable) {
        byte[] command = new byte[]{27, 123, enable.getValue()};
        this.addArrayToCommand(command);
    }

    public void addSetCharcterSize(FYEscCommand.WIDTH_ZOOM width, FYEscCommand.HEIGHT_ZOOM height) {
        byte[] command = new byte[]{29, 33, 0};
        byte temp = 0;
        temp = (byte)(temp | width.getValue());
        temp |= height.getValue();
        command[2] = temp;
        this.addArrayToCommand(command);
    }

    public void addTurnReverseModeOnOrOff(FYEscCommand.ENABLE enable) {
        byte[] command = new byte[]{29, 66, enable.getValue()};
        this.addArrayToCommand(command);
    }

    public void addSelectPrintingPositionForHRICharacters(FYEscCommand.HRI_POSITION position) {
        byte[] command = new byte[]{29, 72, position.getValue()};
        this.addArrayToCommand(command);
    }

    public void addSetLeftMargin(short n) {
        byte[] command = new byte[]{29, 76, 0, 0};
        byte nl = (byte)(n % 256);
        byte nh = (byte)(n / 256);
        command[2] = nl;
        command[3] = nh;
        this.addArrayToCommand(command);
    }

    public void addSetHorAndVerMotionUnits(byte x, byte y) {
        byte[] command = new byte[]{29, 80, x, y};
        this.addArrayToCommand(command);
    }

    public void addCutAndFeedPaper(byte length) {
        byte[] command = new byte[]{29, 86, 66, length};
        this.addArrayToCommand(command);
    }

    public void addCutPaper() {
        byte[] command = new byte[]{29, 86, 1};
        this.addArrayToCommand(command);
    }

    public void addSetPrintingAreaWidth(short width) {
        byte nl = (byte)(width % 256);
        byte nh = (byte)(width / 256);
        byte[] command = new byte[]{29, 87, nl, nh};
        this.addArrayToCommand(command);
    }

    public void addSetAutoSatusBack(FYEscCommand.ENABLE enable) {
        byte[] command = new byte[]{29, 97, 0};
        if (enable == FYEscCommand.ENABLE.OFF) {
            command[2] = 0;
        } else {
            command[2] = -1;
        }

        this.addArrayToCommand(command);
    }

    public void addSetFontForHRICharacter(FYEscCommand.FONT font) {
        byte[] command = new byte[]{29, 102, font.getValue()};
        this.addArrayToCommand(command);
    }

    public void addSetBarcodeHeight(byte height) {
        byte[] command = new byte[]{29, 104, height};
        this.addArrayToCommand(command);
    }

    public void addSetBarcodeWidth(byte width) {
        byte[] command = new byte[]{29, 119, 0};
        if (width > 6) {
            width = 6;
        }

        if (width < 2) {
            width = 1;
        }

        command[2] = width;
        this.addArrayToCommand(command);
    }

    public void addSetKanjiFontMode(FYEscCommand.ENABLE DoubleWidth, FYEscCommand.ENABLE DoubleHeight, FYEscCommand.ENABLE Underline) {
        byte[] command = new byte[]{28, 33, 0};
        byte temp = 0;
        if (DoubleWidth == FYEscCommand.ENABLE.ON) {
            temp = (byte)(temp | 4);
        }

        if (DoubleHeight == FYEscCommand.ENABLE.ON) {
            temp = (byte)(temp | 8);
        }

        if (Underline == FYEscCommand.ENABLE.ON) {
            temp = (byte)(temp | 128);
        }

        command[2] = temp;
        this.addArrayToCommand(command);
    }

    public void addSelectKanjiMode() {
        byte[] command = new byte[]{28, 38};
        this.addArrayToCommand(command);
    }

    public void addSetKanjiUnderLine(FYEscCommand.UNDERLINE_MODE underline) {
        byte[] command = new byte[]{28, 45, 0};
        command[3] = underline.getValue();
        this.addArrayToCommand(command);
    }

    public void addCancelKanjiMode() {
        byte[] command = new byte[]{28, 46};
        this.addArrayToCommand(command);
    }

    public void addSetKanjiLefttandRightSpace(byte left, byte right) {
        byte[] command = new byte[]{28, 83, left, right};
        this.addArrayToCommand(command);
    }

    public void addSetQuadrupleModeForKanji(FYEscCommand.ENABLE enable) {
        byte[] command = new byte[]{28, 87, enable.getValue()};
        this.addArrayToCommand(command);
    }

    public void addRastBitImage(Bitmap bitmap, int nWidth, int nMode) {
        if (bitmap != null) {
            int width = (nWidth + 7) / 8 * 8;
            int height = bitmap.getHeight() * width / bitmap.getWidth();
            Bitmap grayBitmap = GpUtils.toGrayscale(bitmap);
            Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
            byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
            byte[] command = new byte[8];
            height = src.length / width;
            command[0] = 29;
            command[1] = 118;
            command[2] = 48;
            command[3] = (byte)(nMode & 1);
            command[4] = (byte)(width / 8 % 256);
            command[5] = (byte)(width / 8 / 256);
            command[6] = (byte)(height % 256);
            command[7] = (byte)(height / 256);
            this.addArrayToCommand(command);
            byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);

            for(int k = 0; k < codecontent.length; ++k) {
                this.Command.add(codecontent[k]);
            }
        } else {
            Log.d("BMP", "bmp.  null ");
        }

    }

    public void addOriginRastBitImage(Bitmap bitmap, int nWidth, int nMode) {
        if (bitmap != null) {
            int width = (nWidth + 7) / 8 * 8;
            int height = bitmap.getHeight() * width / bitmap.getWidth();
            Bitmap rszBitmap = GpUtils.resizeImage(bitmap, width, height);
            byte[] data = GpUtils.printEscDraw(rszBitmap);
            this.addArrayToCommand(data);
        } else {
            Log.d("BMP", "bmp.  null ");
        }

    }

    public void addRastBitImageWithMethod(Bitmap bitmap, int nWidth, int nMode, int method) {
        if (bitmap != null) {
            int width = (nWidth + 7) / 8 * 8;
            int height = bitmap.getHeight() * width / bitmap.getWidth();
            Bitmap resizeImage = GpUtils.resizeImage(bitmap, width, height);
            Bitmap rszBitmap = GpUtils.filter(resizeImage, resizeImage.getWidth(), resizeImage.getHeight());
            byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
            byte[] command = new byte[8];
            height = src.length / width;
            command[0] = 29;
            command[1] = 118;
            command[2] = 48;
            command[3] = (byte)(nMode & 1);
            command[4] = (byte)(width / 8 % 256);
            command[5] = (byte)(width / 8 / 256);
            command[6] = (byte)(height % 256);
            command[7] = (byte)(height / 256);
            this.addArrayToCommand(command);
            byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);

            for(int k = 0; k < codecontent.length; ++k) {
                this.Command.add(codecontent[k]);
            }
        } else {
            Log.d("BMP", "bmp.  null ");
        }

    }

    public void addDownloadNvBitImage(Bitmap[] bitmap) {
        if (bitmap == null) {
            Log.d("BMP", "bmp.  null ");
        } else {
            Log.d("BMP", "bitmap.length " + bitmap.length);
            int n = bitmap.length;
            if (n > 0) {
                byte[] command = new byte[]{28, 113, (byte)n};
                this.addArrayToCommand(command);

                for(int i = 0; i < n; ++i) {
                    int height = (bitmap[i].getHeight() + 7) / 8 * 8;
                    int width = bitmap[i].getWidth() * height / bitmap[i].getHeight();
                    Bitmap grayBitmap = GpUtils.toGrayscale(bitmap[i]);
                    Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
                    byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
                    height = src.length / width;
                    Log.d("BMP", "bmp  Width " + width);
                    Log.d("BMP", "bmp  height " + height);
                    byte[] codecontent = FYGpUtils.pixToEscNvBitImageCmd(src, width, height);

                    for(int k = 0; k < codecontent.length; ++k) {
                        this.Command.add(codecontent[k]);
                    }
                }
            }

        }
    }

    public void addPrintNvBitmap(byte n, byte mode) {
        byte[] command = new byte[]{28, 112, n, mode};
        this.addArrayToCommand(command);
    }

    public void addUPCA(String content) {
        byte[] command = new byte[]{29, 107, 65, 11};
        if (content.length() >= command[3]) {
            this.addArrayToCommand(command);
            this.addStrToCommand(content, 11);
        }
    }

    public void addUPCE(String content) {
        byte[] command = new byte[]{29, 107, 66, 11};
        if (content.length() >= command[3]) {
            this.addArrayToCommand(command);
            this.addStrToCommand(content, command[3]);
        }
    }

    public void addEAN13(String content) {
        byte[] command = new byte[]{29, 107, 67, 12};
        if (content.length() >= command[3]) {
            this.addArrayToCommand(command);
            Log.d("EscCommand", "content.length" + content.length());
            this.addStrToCommand(content, command[3]);
        }
    }

    public void addEAN8(String content) {
        byte[] command = new byte[]{29, 107, 68, 7};
        if (content.length() >= command[3]) {
            this.addArrayToCommand(command);
            this.addStrToCommand(content, command[3]);
        }
    }

    @SuppressLint({"DefaultLocale"})
    public void addCODE39(String content) {
        byte[] command = new byte[]{29, 107, 69, (byte)content.length()};
        content = content.toUpperCase();
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }

    public void addITF(String content) {
        byte[] command = new byte[]{29, 107, 70, (byte)content.length()};
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }

    public void addCODABAR(String content) {
        byte[] command = new byte[]{29, 107, 71, (byte)content.length()};
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }

    public void addCODE93(String content) {
        byte[] command = new byte[]{29, 107, 72, (byte)content.length()};
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }

    public void addCODE128(String content) {
        byte[] command = new byte[]{29, 107, 73, (byte)content.length()};
        this.addArrayToCommand(command);
        this.addStrToCommand(content, command[3]);
    }

    public String genCodeC(String content) {
        List<Byte> bytes = new ArrayList(20);
        int len = content.length();
        bytes.add((byte) 123);
        bytes.add((byte) 67);

        int j;
        for(int i = 0; i < len; i += 2) {
            j = (content.charAt(i) - 48) * 10;
            int bits = content.charAt(i + 1) - 48;
            int current = i + bits;
            bytes.add((byte)current);
        }

        byte[] bb = new byte[bytes.size()];

        for(j = 0; j < bb.length; ++j) {
            bb[j] = (Byte)bytes.get(j);
        }

        return new String(bb, 0, bb.length);
    }

    public String genCodeB(String content) {
        return String.format("{B%s", content);
    }

    public String genCode128(String content) {
        String regex = "([^0-9])";
        String[] str = content.split(regex);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        String splitString = null;
        int strlen = str.length;
        if (strlen > 0 && matcher.find()) {
            splitString = matcher.group(0);
        }

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < strlen; ++i) {
            String first = str[i];
            int len = first.length();
            int result = len % 2;
            if (result == 0) {
                String codeC = this.genCodeC(first);
                sb.append(codeC);
            } else {
                sb.append(this.genCodeB(String.valueOf(first.charAt(0))));
                sb.append(this.genCodeC(first.substring(1, first.length())));
            }

            if (splitString != null) {
                sb.append(this.genCodeB(splitString));
                splitString = null;
            }
        }

        return sb.toString();
    }

    public void addSelectSizeOfModuleForQRCode(byte n) {
        byte[] command = new byte[]{29, 40, 107, 3, 0, 49, 67, 3};
        command[7] = n;
        this.addArrayToCommand(command);
    }

    public void addSelectErrorCorrectionLevelForQRCode(byte n) {
        byte[] command = new byte[]{29, 40, 107, 3, 0, 49, 69, n};
        this.addArrayToCommand(command);
    }

    public void addStoreQRCodeData(String content) {
        byte[] command = new byte[]{29, 40, 107, (byte)((content.getBytes().length + 3) % 256), (byte)((content.getBytes().length + 3) / 256), 49, 80, 48};
        this.addArrayToCommand(command);
        byte[] bs = null;
        if (!content.equals("")) {
            try {
                bs = content.getBytes("utf-8");
            } catch (UnsupportedEncodingException var5) {
                var5.printStackTrace();
            }

            for(int i = 0; i < bs.length; ++i) {
                this.Command.add(bs[i]);
            }
        }

    }

    public void addPrintQRCode() {
        byte[] command = new byte[]{29, 40, 107, 3, 0, 49, 81, 48};
        this.addArrayToCommand(command);
    }

    public void addQueryPrinterStatus() {
        byte[] command = new byte[]{16, 4, 2};
        this.addArrayToCommand(command);
    }

    public void addUserCommand(byte[] command) {
        this.addArrayToCommand(command);
    }

    public static enum CHARACTER_SET {
        USA(0),
        FRANCE(1),
        GERMANY(2),
        UK(3),
        DENMARK_I(4),
        SWEDEN(5),
        ITALY(6),
        SPAIN_I(7),
        JAPAN(8),
        NORWAY(9),
        DENMARK_II(10),
        SPAIN_II(11),
        LATIN_AMERCIA(12),
        KOREAN(13),
        SLOVENIA(14),
        CHINA(15);

        private final int value;

        private CHARACTER_SET(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }

    public static enum CODEPAGE {
        PC437(0),
        KATAKANA(1),
        PC850(2),
        PC860(3),
        PC863(4),
        PC865(5),
        WEST_EUROPE(6),
        GREEK(7),
        HEBREW(8),
        EAST_EUROPE(9),
        IRAN(10),
        WPC1252(16),
        PC866(17),
        PC852(18),
        PC858(19),
        IRANII(20),
        LATVIAN(21),
        ARABIC(22),
        PT151(23),
        PC747(24),
        WPC1257(25),
        VIETNAM(27),
        PC864(28),
        PC1001(29),
        UYGUR(30),
        THAI(255);

        private final int value;

        private CODEPAGE(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }

    public static enum ENABLE {
        OFF(0),
        ON(1);

        private final int value;

        private ENABLE(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }

    public static enum FONT {
        FONTA(0),
        FONTB(1);

        private final int value;

        private FONT(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }

    public static enum HEIGHT_ZOOM {
        MUL_1(0),
        MUL_2(1),
        MUL_3(2),
        MUL_4(3),
        MUL_5(4),
        MUL_6(5),
        MUL_7(6),
        MUL_8(7);

        private final int value;

        private HEIGHT_ZOOM(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }

    public static enum HRI_POSITION {
        NO_PRINT(0),
        ABOVE(1),
        BELOW(2),
        ABOVE_AND_BELOW(3);

        private final int value;

        private HRI_POSITION(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }

    public static enum JUSTIFICATION {
        LEFT(0),
        CENTER(1),
        RIGHT(2);

        private final int value;

        private JUSTIFICATION(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }

    public static enum STATUS {
        PRINTER_STATUS(1),
        PRINTER_OFFLINE(2),
        PRINTER_ERROR(3),
        PRINTER_PAPER(4);

        private final int value;

        private STATUS(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }

    public static enum UNDERLINE_MODE {
        OFF(0),
        UNDERLINE_1DOT(1),
        UNDERLINE_2DOT(2);

        private final int value;

        private UNDERLINE_MODE(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }

    public static enum WIDTH_ZOOM {
        MUL_1(0),
        MUL_2(16),
        MUL_3(32),
        MUL_4(48),
        MUL_5(64),
        MUL_6(80),
        MUL_7(96),
        MUL_8(112);

        private final int value;

        private WIDTH_ZOOM(int value) {
            this.value = value;
        }

        public byte getValue() {
            return (byte)this.value;
        }
    }
}
