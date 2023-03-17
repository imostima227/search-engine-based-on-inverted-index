package hust.cs.javacourse.search.run;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.impl.DocumentBuilder;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.index.impl.IndexBuilder;
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
 * 基于内存的搜索引擎测试
 */
public class App {
    /**
     * 根据根目录下的文件构建索引
     * @return
     */
    static AbstractIndex BuildIndex(){
        AbstractDocumentBuilder documentBuilder = new DocumentBuilder();
        AbstractIndexBuilder indexBuilder = new IndexBuilder(documentBuilder);
        String rootDir = Config.DOC_DIR;
        System.out.println("Start build index ...");
        AbstractIndex index = indexBuilder.buildIndex(rootDir);
        index.optimize();
        String indexFile = Config.INDEX_DIR + "index.dat";
        index.save(new File(indexFile));
        AbstractIndex index2 = new Index();
        index2.load(new File(indexFile));
        return index2;
    }

    /**
     * 程序测试入口
     * @param args
     */
    public static void main(String[] args){
        boolean flag = true;
        boolean hasBuiltIndex = false;
        while(flag){
            Scanner input = new Scanner(System.in);
            System.out.println("Please input a number to select test scenario:");
            System.out.println("1.IndexBuilderTest");
            System.out.println("2.SearchOneKeyWordTest");
            System.out.println("3.SearchTwoKeyWordTest");
            System.out.println("4.SearchPhraseTest");
            System.out.println("0.exit");
            int i=input.nextInt();
            switch(i){
                case 0://退出
                    flag = false;
                    break;
                case 1://构建索引
                    System.out.println(BuildIndex());
                    hasBuiltIndex = true;
                    break;
                case 2://搜索一个单词
                    if(hasBuiltIndex == false){
                        BuildIndex();
                        hasBuiltIndex = true;
                    }
                    Sort simpleSorter = new SimpleSorter();
                    String indexFile = Config.INDEX_DIR + "index.dat";
                    AbstractIndexSearcher searcher = new IndexSearcher();
                    searcher.open(indexFile);
                    AbstractHit[] hits;
                    boolean op1 = true;
                    while(true){
                        System.out.println("Please input a word:");
                        String buf;
                        if(op1) buf = input.nextLine();
                        String word = input.nextLine();
                        StringSplitter splitter=new StringSplitter();
                        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
                        List<String> term= splitter.splitByRegex(word);
                        if(term.size() == 1){
                            hits = searcher.search(new Term(word), simpleSorter);
                            break;
                        }
                        else{
                            System.out.println("Wrong!Please input only one word.");
                            op1 = false;
                        }
                    }
                    for (AbstractHit hit : hits) {
                        System.out.println(hit);
                    }
                    break;
                case 3://搜索两个单词
                    if(hasBuiltIndex == false){
                        BuildIndex();
                        hasBuiltIndex = true;
                    }
                    Sort simpleSorter1 = new SimpleSorter();
                    String indexFile1 = Config.INDEX_DIR + "index.dat";
                    AbstractIndexSearcher searcher1 = new IndexSearcher();
                    searcher1.open(indexFile1);
                    AbstractHit[] hits1;
                    boolean op2 = true;
                    while(true){
                        System.out.println("Please input two word:");
                        String buf;
                        if(op2) buf = input.nextLine();
                        String word = input.nextLine();
                        StringSplitter splitter=new StringSplitter();
                        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
                        List<String> term= splitter.splitByRegex(word);
                        if(term.size() == 3 &&term.get(1).equals("OR")){
                            hits = searcher1.search(new Term(term.get(0)),new Term(term.get(2)),simpleSorter1, AbstractIndexSearcher.LogicalCombination.OR);
                            break;
                        }
                        else if(term.size()==3&&term.get(1).equals("AND")){
                            hits = searcher1.search(new Term(term.get(0)), new Term(term.get(2)), simpleSorter1, AbstractIndexSearcher.LogicalCombination.AND);
                            break;
                        }
                        else{
                            System.out.println("Wrong!Please input two words.");
                            op2 = false;
                        }
                    }
                    for (AbstractHit hit : hits) {
                        System.out.println(hit);
                    }
                    break;
                case 4://搜索短语
                    if(hasBuiltIndex == false){
                        BuildIndex();
                        hasBuiltIndex = true;
                    }
                    Sort simpleSorter2 = new SimpleSorter();
                    String indexFile2 = Config.INDEX_DIR + "index.dat";
                    IndexSearcher searcher2 = new IndexSearcher();
                    searcher2.open(indexFile2);
                    AbstractHit[] hits2;
                    boolean op3 = true;
                    while(true){
                        System.out.println("Please input a phrase of two words:");
                        String buf;
                        if(op3) buf = input.nextLine();
                        String word = input.nextLine();
                        StringSplitter splitter=new StringSplitter();
                        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
                        List<String> term= splitter.splitByRegex(word);
                        if(term.size() == 2){
                            hits = searcher2.search(new Term(term.get(0)),new Term(term.get(1)),simpleSorter2);
                            break;
                        }
                        else{
                            System.out.println("Wrong!Please input a phrase of two words.");
                            op3 = false;
                        }
                    }
                    for (AbstractHit hit : hits) {
                        System.out.println(hit);
                    }
                    break;
                default:
                    System.out.println("Please input valid number.");
                    break;
            }
        }
    }

}
