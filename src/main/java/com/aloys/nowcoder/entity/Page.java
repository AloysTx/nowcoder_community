package com.aloys.nowcoder.entity;
/*
    封装分页相关内容
*/
public class Page {
    // 当前页码
    private int current = 1;
    // 显示上限
    private int limit = 10;
    // 数据总数(用于计算总页数)
    private int rows;
    // 查询路径(用于复用分页链接)
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        // 当前页码 >= 1
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        // 限制每页显示页码数
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 计算当前页的起始索引
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数 = 总记录数 / limit
     *
     * @return
     */
    public int getTotal() {
        // rows / limit [+1]
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            // 余下的不足 limit 行的额外占一页
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码（就是跳到某一页后，页面下方显示的页码的最小值，设置为当前页码 -2）
     *
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        // 在当前页为 1 或 2 时，计算出来的起始页码小于1，有错，需要设置为 1
        return Math.max(from, 1);
    }

    /**
     * 获取结束页码（显示页码的最大值，设置为当前页码 +2）
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        // 防止计算出的结束页码超过总页数
        return Math.min(to, total);
    }

}
