package com.hiep.product.common;

import lombok.Data;

@Data
public class Pagination {

    /** 1. ページことに表示する掲示物数 **/
    private int pageSize = 5;

    /** 2. ページングした、ブロック数 **/
    private int blockSize = 10;

    /** 3. 現在ページ **/
    private int page = 1;

    /** 4. 現在ブロック **/
    private int block = 1;

    /** 5. 総掲示物数 **/
    private int totalListCnt;

    /** 6. 総ページ数 **/
    private int totalPageCnt;

    /** 7. 総ブロック数 **/
    private int totalBlockCnt;

    /** 8. ブロックスタートページ **/
    private int startPage = 1;

    /** 9. ブロック最後ページ **/
    private int endPage = 1;

    /** 10. DB接近スタートインデックス **/
    private int startIndex = 0;

    /** 11. 以前ブロックの最後ページ **/
    private int prevBlock;

    /** 12. 次のブロックの最後ページ **/
    private int nextBlock;

    // Getter・Setter省略

    public Pagination(int totalListCnt, int page) {

        // 総掲示物数と現在ページはコントローラーからもらう。
        // 総掲示物数  - totalListCnt
        // 現在ページ  - page

        /** 3. 現在ページ **/
        setPage(page);

        /** 5. 総掲示物数 **/
        setTotalListCnt(totalListCnt);

        /** 6. 総ページ数 **/
        setTotalPageCnt((int) Math.ceil(totalListCnt * 1.0 / pageSize));

        /** 7. 総ブロック数 **/
        setTotalBlockCnt((int) Math.ceil(totalPageCnt * 1.0 / blockSize));

        /** 4. 現在ブロック **/
        setBlock((int) Math.ceil((page * 1.0)/blockSize)); 

        /** 8. ブロックスタートページ **/
        setStartPage((block - 1) * blockSize + 1);

        /** 9. ブロック最後ページ **/
        setEndPage(startPage + blockSize - 1);

        /* === ブラック最後ページについてバリエーション ===*/
        if(endPage > totalPageCnt){this.endPage = totalPageCnt;}

        /** 11. 以前ブロックの最後ページ **/
        setPrevBlock((block * blockSize) - blockSize);

        /* === 以前ブロックについてバリエーション === */
        if(prevBlock < 1) {this.prevBlock = 1;}

        /** 12. 次のブロックの最後ページ **/
        setNextBlock((block * blockSize) + 1);

        /* === 次のブロックについてバリエーション ===*/
        if(nextBlock > totalPageCnt) {nextBlock = totalPageCnt;}

        /** 10. DB接近スタートインデックス **/
        setStartIndex((page-1) * pageSize);

    }
}