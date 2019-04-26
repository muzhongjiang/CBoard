package org.cboard.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by yfyuan on 2017/5/5.
 */
@Getter
@Setter
@ToString
public class DashboardBoardParam {
    private Long id;
    private String userId;
    private Long boardId;
    private String config;

}
