package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Spliterator;

/**
 * 测试搜索
 */
public class TestSearchIndex {
    /**
     *  搜索程序入口
     * @param args ：命令行参数
     */
    public static void main(String[] args){

            Sort simpleSorter = new SimpleSorter();
            String indexFile = Config.INDEX_DIR + "index.dat";
            AbstractIndexSearcher searcher = new IndexSearcher();
            searcher.open(indexFile);
            AbstractHit[] hits = searcher.search(new Term("death"),new Term("rate"), simpleSorter, AbstractIndexSearcher.LogicalCombination.OR);
            //AbstractHit[] hits = searcher.search(new Term("government"), simpleSorter);
            for(AbstractHit hit : hits){
                System.out.println(hit);
            }


    }
}
/*coronavirus
wales
circumstances
government
google
health
luxembourg
recognition
wales
all-star
kobe*/
