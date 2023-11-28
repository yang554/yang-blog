package com.yang.blog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel(value="t_blog", description="博客实体类")
public class TBlogEntity{
    @ApiModelProperty(value = "编码")
    private Long id;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(value = "封面")
    private String cover;
    @ApiModelProperty(value = "是否原创")
    private String isoriginal;
    @ApiModelProperty(value = "是否公开")
    private Boolean ispublic;
    @ApiModelProperty(value = "是否开启评论")
    private Boolean iscomment;
    @ApiModelProperty(value = "是否发布")
    private Boolean issue;
    @ApiModelProperty(value = "浏览量")
    private String browsecount;
    @ApiModelProperty(value = "点赞量")
    private String likes;
    @ApiModelProperty(value = "收藏量")
    private String collection;
    @ApiModelProperty(value = "分类编码")
    private Long typeid;
    @ApiModelProperty(value = "用户编码")
    private Long userid;
    @ApiModelProperty(value = "标签json")
    private String tagjson;
    @ApiModelProperty(value = "是否物理删除")
    private Boolean isdel;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createtime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updtime;
    @ApiModelProperty(value = "备注")
    private String note;
    @ApiModelProperty(value = "备用")
    private String data;
    @ApiModelProperty(value = "用户名称")
    private String uName;
    @ApiModelProperty(value = "用户昵称")
    private String uNickname;
    @ApiModelProperty(value = "博客类型名称")
    private String tName;
    @TableField(exist = false)  //查询时过滤非数据库字段
    @ApiModelProperty(value = "博客标签集合")
    private List<TTagEntity> blogTags;

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuNickname() {
        return uNickname;
    }

    public void setuNickname(String uNickname) {
        this.uNickname = uNickname;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getIsoriginal() {
        return isoriginal;
    }

    public void setIsoriginal(String isoriginal) {
        this.isoriginal = isoriginal;
    }

    public Boolean getIspublic() {
        return ispublic;
    }

    public void setIspublic(Boolean ispublic) {
        this.ispublic = ispublic;
    }

    public Boolean getIscomment() {
        return iscomment;
    }

    public void setIscomment(Boolean iscomment) {
        this.iscomment = iscomment;
    }

    public Boolean getIssue() {
        return issue;
    }

    public void setIssue(Boolean issue) {
        this.issue = issue;
    }

    public String getBrowsecount() {
        return browsecount;
    }

    public void setBrowsecount(String browsecount) {
        this.browsecount = browsecount;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Long getTypeid() {
        return typeid;
    }

    public void setTypeid(Long typeid) {
        this.typeid = typeid;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getTagjson() {
        return tagjson;
    }

    public void setTagjson(String tagjson) {
        this.tagjson = tagjson;
    }

    public Boolean getIsdel() {
        return isdel;
    }

    public void setIsdel(Boolean isdel) {
        this.isdel = isdel;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getUpdtime() {
        return updtime;
    }

    public void setUpdtime(LocalDateTime updtime) {
        this.updtime = updtime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<TTagEntity> getBlogTags() {
        return blogTags;
    }

    public void setBlogTags(List<TTagEntity> blogTags) {
        this.blogTags = blogTags;
    }
}
