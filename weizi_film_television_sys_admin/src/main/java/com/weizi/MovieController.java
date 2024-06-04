package com.weizi;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.dto.MovieDTO;
import com.weizi.common.domain.dto.pageParam.MovieParamDTO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.domain.vo.list.MovieVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.MovieService;
import com.weizi.common.utils.JwtUtils;
import com.weizi.common.utils.fileOperationUtils.FileOperationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

/**
 * @date AWei
 * @date 2024/05/21
 */
@RestController
@RequestMapping("admin/movie")
@Slf4j
public class MovieController {
    private final MovieService movieService;
    private final FileOperationUtils fileOperationUtils;
    private final JwtUtils jwtUtils;

    public MovieController(MovieService movieService, FileOperationUtils fileOperationUtils, JwtUtils jwtUtils) {
        this.movieService = movieService;
        this.fileOperationUtils = fileOperationUtils;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 获取影片列表
     */
    @GetMapping("list")
    public WeiZiResult selectList(MovieParamDTO movieParamDto) {
        WeiZiPageResult<MovieVO> movieList = movieService.selectList(movieParamDto);
        return WeiZiResult.success(movieList);
    }

    /**
     * 获取单个影片数据（用于播放准备）
     */
    @GetMapping("getMovieUrl")
    public WeiZiResult getMovieUrl(@Valid @NotNull(message = "movieMd5不可为空！") @RequestParam("movieMd5") String movieMd5) {
        String movieUrl = movieService.getMovieUrl(movieMd5);
        if (ObjectUtil.isNull(movieUrl)) return WeiZiResult.error("该影片不存在！");
        return WeiZiResult.success(movieUrl);
    }

    @GetMapping("/get/movie/{token}/{movieMd5}/m3u8")
    public ResponseEntity<InputStreamResource> getM3u8(@Valid @NotNull(message = "token不可为空！") @PathVariable String token, @Valid @NotNull(message = "movieMd5不可为空！") @PathVariable("movieMd5") String movieMd5) {
            // 获取M3U8文件的输入流
            InputStream m3u8InputStream = null;
            // 检测token是否存在并且验证token
            if (ObjectUtil.isNotNull(jwtUtils.getLoginAdminVOAndCheckToken(token))) m3u8InputStream = fileOperationUtils.getFileStreamByMovieMd5(movieMd5);
            if (m3u8InputStream == null) {
                log.error("无法获取m3u8文件的输入流");
                return ResponseEntity.status(HttpStatus.ERROR).body(null);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(m3u8InputStream));
    }

    @GetMapping("/get/movie/{token}/{movieMd5}/{chunk}")
    public ResponseEntity<InputStreamResource> getTsChunk(@Valid @NotNull(message = "token不可为空！") @PathVariable String token, @PathVariable("movieMd5") String movieMd5, @PathVariable String chunk) {
        if (ObjectUtil.isNull(movieMd5)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        if (ObjectUtil.isNull(chunk)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        // 获取TS切片文件的输入流
        InputStream tsInputStream = null;
        // 检测token是否存在并且验证token
        if (ObjectUtil.isNotNull(jwtUtils.getLoginAdminVOAndCheckToken(token))) tsInputStream = fileOperationUtils.getFileStreamTsByMovieMd5(movieMd5, chunk);
        if (tsInputStream == null) {
            log.error("无法获取TS切片输入流");
            return ResponseEntity.status(HttpStatus.ERROR).body(null);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // 更改为适合二进制数据的类型
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(tsInputStream));
    }

    @GetMapping("delete")
    public WeiZiResult delete(@RequestParam("movieMd5") String movieMd5) {
        // 删除合并电影，这里涉及到删除数据库和文件等操作
        if (movieService.deleteMergeMovie(movieMd5))
            return WeiZiResult.success("删除成功");
        return WeiZiResult.error("删除失败");
    }

    /**
     * 获取单个详情
     */
    /*@GetMapping("getDetail")
    public WeiZiResult searchMovieById(@RequestParam("movieId") Long movieId) {
        if (ObjectUtil.isNull(movieId)) {
            return WeiZiResult.error("movieId不可为空！");
        }
        MovieVO movie = movieService.searchMovieById(movieId);
        return WeiZiResult.error("该影片不存在！");
    }*/

    /*@PostMapping("uploadAvatar")
    public WeiZiResult uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("movieId") Long movieId) throws IOException {
        if (ObjectUtil.isEmpty(file)) {
            return WeiZiResult.error("上传文件为空");
        }

        // 获取上传文件的大小
        long fileSize = file.getSize();
        // 获取上传文件的类型
        String fileType = file.getContentType();

        // 判断文件大小是否符合要求
        long maxSize = parseSize(maxAvatarSize);
        if (fileSize > maxSize) {
            return WeiZiResult.error("文件大小超过限制（" + maxAvatarSize + "）");
        }

        // 判断文件类型是否符合要求
        if (!supportedAvatarTypes.contains(fileType) || ObjectUtil.isNull(fileType)) {
            return WeiZiResult.error("文件类型不支持");
        }
        String imageFileName = imageUtils.uploadImage(file, FileConstants.ADMIN_AVATAR);
        // 判断文件名是否为空
        if (ObjectUtil.isNotNull(imageFileName)) {
            // 先获取原本的头像数据
            MoviePO movie = movieMapper.selectById(movieId);
            // 更新成功后删除原本的头像文件，防止冗余
            if (movieService.updateMovieAvatar(imageFileName, movieId)) {
                if (ObjectUtil.isNotNull(movie) && ObjectUtil.isNotNull(movie.getAvatar()))
                    imageUtils.deleteImage(movie.getAvatar(), FileConstants.ADMIN_AVATAR);
                // 获取头像文件路径
                String avatarFilePath = avatarPath + imageFileName;
                String extension = FilenameUtils.getExtension(avatarFilePath);
                // 将头像文件转换为 Base64 编码的字符串并设置到实体对象中
                return WeiZiResult.success("上传成功", "data:image/" + extension.toLowerCase() + ";base64," + imageUtils.encodeImageToBase64(avatarFilePath));
            }
        }
        return WeiZiResult.error("文件上传失败");
    }*/

    // FIXME 后续加入上传电影或海报等就修改成公共方法
    private long parseSize(String sizeStr) {
        String numberPart = sizeStr.substring(0, sizeStr.length() - 2);
        String unitPart = sizeStr.substring(sizeStr.length() - 2).toUpperCase();
        long multiplier = switch (unitPart) {
            case "KB" -> 1024;
            case "MB" -> 1024 * 1024;
            case "GB" -> 1024 * 1024 * 1024;
            default -> throw new IllegalArgumentException("Invalid size unit: " + unitPart);
        };
        return Long.parseLong(numberPart) * multiplier;
    }
}
