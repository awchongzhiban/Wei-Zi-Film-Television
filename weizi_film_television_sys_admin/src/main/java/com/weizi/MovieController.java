package com.weizi;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.config.MinioConfig;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.dto.dataParam.MovieGroupParam;
import com.weizi.common.domain.dto.dataParam.MovieParam;
import com.weizi.common.domain.dto.pageParam.MovieParamDTO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.domain.vo.list.MovieVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.GenreService;
import com.weizi.common.service.MovieService;
import com.weizi.common.utils.JwtUtils;
import com.weizi.common.utils.fileOperationUtils.FileOperationUtils;
import com.weizi.common.utils.imageutils.ImageUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @date AWei
 * @date 2024/05/21
 */
@RestController
@RequestMapping("admin/movie")
@Slf4j
public class MovieController {
    @Value("${file-upload.poster.max-size}")
    private String maxPosterSize;

    @Value("${file-upload.poster.image-types}")
    private List<String> supportedPosterTypes;
    
    // 图片工具类
    @Autowired
    private ImageUtils imageUtils;
    
    private final MovieService movieService;
    private final GenreService genreService;
    private final FileOperationUtils fileOperationUtils;
    private final JwtUtils jwtUtils;
    private final MinioConfig minioConfig;

    public MovieController(MovieService movieService, GenreService genreService, FileOperationUtils fileOperationUtils, JwtUtils jwtUtils, MinioConfig minioConfig) {
        this.movieService = movieService;
        this.genreService = genreService;
        this.fileOperationUtils = fileOperationUtils;
        this.jwtUtils = jwtUtils;
        this.minioConfig = minioConfig;
    }

    /**
     * 获取影片列表
     */
    @GetMapping("list")
    public WeiZiResult selectList(MovieParamDTO movieParamDTO) {
        WeiZiPageResult<MovieVO> movieList = movieService.selectList(movieParamDTO);
        return WeiZiResult.success(movieList);
    }

    @GetMapping("list/getChannelMap")
    public WeiZiResult getChannelMap() {
        Map<Long, String> getChannelMap = movieService.selectAllChannels();
        return WeiZiResult.success(getChannelMap);
    }

    @GetMapping("list/getGenreTagMap")
    public WeiZiResult getGenreTagMap() {
        Map<Long, String> getGenreTagMap = movieService.selectAllGenres();
        return WeiZiResult.success(getGenreTagMap);
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
    @GetMapping("getDetail")
    public WeiZiResult searchMovieById(@RequestParam("movieId") Long movieId) {
        if (ObjectUtil.isNull(movieId)) {
            return WeiZiResult.error("movieId不可为空！");
        }
        MovieParam movieParam = movieService.searchMovieParamById(movieId);
        if (ObjectUtil.isNotEmpty(movieParam)) return WeiZiResult.success(movieParam);
        return WeiZiResult.error("该影片不存在！");
    }

    @PostMapping("updateMovieGroup")
    public WeiZiResult updateMovieGroup(@Valid @RequestBody MovieGroupParam movieGroupParam) {
        if (movieService.updateMovieGroup(movieGroupParam))
            return WeiZiResult.success("更新成功");
        return WeiZiResult.error("更新失败");
    }

    /**
     * 更新影片
     */
    @PostMapping("update")
    public WeiZiResult update(@RequestBody MovieParam movieParam) {
        if (ObjectUtil.isNotEmpty(movieParam)) {
            if (ObjectUtil.isEmpty(movieParam.getMovieId())) return WeiZiResult.error("影片ID不可为空");
            if (ObjectUtil.isEmpty(movieParam.getMovieName())) return WeiZiResult.error("影片名称不可为空");
            if (ObjectUtil.isEmpty(movieParam.getChannelId())) return WeiZiResult.error("频道ID不可为空");
            if (ObjectUtil.isEmpty(movieParam.getGenreIdList())) return WeiZiResult.error("影片类型不可为空");
            if (!genreService.isNonExistentGenreId(movieParam.getGenreIdList())) return WeiZiResult.error("影片类型不存在");
            if (movieService.updateMovie(movieParam))
                return WeiZiResult.success("更新成功");
            return WeiZiResult.error("更新失败");
        }
        return WeiZiResult.error("数据不可为空");
    }

    @PostMapping("uploadMainPoster")
    public WeiZiResult uploadMainPoster(@RequestParam("file") MultipartFile file, @RequestParam("movieId") Long movieId) throws IOException {
        if (ObjectUtil.isEmpty(file)) {
            return WeiZiResult.error("上传文件为空");
        }
        // 判断文件大小是否符合要求
        long maxSize = imageUtils.parseSize(maxPosterSize);
        if (file.getSize() > maxSize) {
            return WeiZiResult.error("文件大小超过限制（" + maxPosterSize + "）");
        }
        // 获取上传文件的类型
        String fileType = file.getContentType();
        // 判断文件类型是否符合要求
        if (!supportedPosterTypes.contains(fileType) || ObjectUtil.isNull(fileType)) {
            return WeiZiResult.error("文件类型不支持");
        }
        String imageFileName = file.getOriginalFilename();
        // 判断文件名是否为空
        if (ObjectUtil.isNotNull(imageFileName)) {
            // 先获取原本的主海报数据
            MoviePO movie = movieService.selectById(movieId);
            String posterOriginal = movie.getMainPoster();
            String movieMd5 = movie.getMovieMd5();
            String concatOriginal = null;
            if (posterOriginal != null) concatOriginal = movieMd5.concat("/" + posterOriginal);
            // 删除文件并上传文件和更新影片海报数据
            if (imageUtils.deleteAndSaveImage(movieMd5.concat("/" + imageFileName), concatOriginal, file, minioConfig.getBucketNameMovieMainPoster())
                    && movieService.updateMovieMainPoster(imageFileName, movieId))
                return WeiZiResult.success("上传成功");
        }
        return WeiZiResult.error("文件上传失败");
    }



    @PostMapping("uploadPoster")
    public WeiZiResult uploadPoster(@RequestParam("file") MultipartFile file, @RequestParam("movieId") Long movieId) throws IOException {
        if (ObjectUtil.isEmpty(file)) {
            return WeiZiResult.error("上传文件为空");
        }
        // 判断文件大小是否符合要求
        long maxSize = imageUtils.parseSize(maxPosterSize);
        if (file.getSize() > maxSize) {
            return WeiZiResult.error("文件大小超过限制（" + maxPosterSize + "）");
        }
        // 获取上传文件的类型
        String fileType = file.getContentType();
        // 判断文件类型是否符合要求
        if (!supportedPosterTypes.contains(fileType) || ObjectUtil.isNull(fileType)) {
            return WeiZiResult.error("文件类型不支持");
        }
        String imageFileName = file.getOriginalFilename();
        // 判断文件名是否为空
        if (ObjectUtil.isNotNull(imageFileName)) {
            // 先获取原本的海报数据
            MoviePO movie = movieService.selectById(movieId);
            String posterOriginal = movie.getPoster();
            String movieMd5 = movie.getMovieMd5();
            String concatOriginal = null;
            if (posterOriginal != null) concatOriginal = movieMd5.concat("/" + posterOriginal);
            // 删除文件并上传文件和更新影片海报数据
            if (imageUtils.deleteAndSaveImage(movieMd5.concat("/" + imageFileName), concatOriginal, file, minioConfig.getBucketNameMoviePoster())
                    && movieService.updateMoviePoster(imageFileName, movieId))
                return WeiZiResult.success("上传成功");
        }
        return WeiZiResult.error("文件上传失败");
    }
}
