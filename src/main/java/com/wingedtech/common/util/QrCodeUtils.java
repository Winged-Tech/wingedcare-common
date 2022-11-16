package com.wingedtech.common.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wingedtech.common.errors.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO description
 *
 * @author Jason
 * @since 2019-04-03 16:27
 */
@Slf4j
public class QrCodeUtils {

    private static final QRCodeWriter QR_CODE_WRITER = new QRCodeWriter();

    /**
     * 生成默认二维码
     *
     * @param content
     * @return
     */
    public static InputStream generateDefaultQrCode(String content) {
        return generateQrCode(new QrCodeInformation(content, 250, 250, 0, ErrorCorrectionLevel.Q));
    }

    /**
     * 生成自定义二维码
     *
     * @param dto {@link QrCodeInformation}
     * @return
     */
    public static InputStream generateQrCode(QrCodeInformation dto) {
        if (StringUtils.isEmpty(dto.getContent())) {
            throw new BusinessException("二维码内容不能为空");
        }
        if (dto.getHeight() == null || dto.getWidth() == null) {
            throw new BusinessException("二维码大小不能为空");
        }
        InputStream qrCode;
        try {
            qrCode = getQrCode(dto);
        } catch (WriterException | IOException e) {
            log.error("create qr code with exception", e);
            throw new BusinessException("创建二维码失败");
        }
        return qrCode;
    }

    /**
     * 二维码相关设定参数
     */
    @Data
    @AllArgsConstructor
    private static class QrCodeInformation {

        /**
         * 二维码内容
         */
        @NotNull
        private String content;

        /**
         * 二维码宽度
         */
        @NotNull
        private Integer width;

        /**
         * 二维码高度
         */
        @NotNull
        private Integer height;

        /**
         * 二维码边距, 可为空
         */
        private Integer margin;

        /**
         * 二维码容错等级, 越高像素点越多
         */
        private ErrorCorrectionLevel errorCorrectionLevel;
    }

    private static InputStream getQrCode(QrCodeInformation informationDTO) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        ErrorCorrectionLevel errorCorrectionLevel = informationDTO.getErrorCorrectionLevel();
        if (errorCorrectionLevel != null) {
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel.name());
        }
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, informationDTO.getMargin() == null ? 0 : informationDTO.getMargin());
        BitMatrix encode = QR_CODE_WRITER.encode(informationDTO.getContent(), BarcodeFormat.QR_CODE, informationDTO.getWidth(), informationDTO.getHeight(), hints);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(encode, "jpg", out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
