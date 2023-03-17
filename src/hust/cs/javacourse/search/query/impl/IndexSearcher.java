package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AbstractIndexSearcher的具体实现类
 */
public class IndexSearcher extends AbstractIndexSearcher {
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        this.index.load(new File(indexFile));
        this.index.optimize();
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        AbstractPostingList postList=index.search(queryTerm);
        if(postList==null) return new AbstractHit[0];
        List<AbstractHit> hitList= new ArrayList<>();
        for(int i=0;i<postList.size();i++){
            AbstractPosting post=postList.get(i);
            Map<AbstractTerm,AbstractPosting> map= new HashMap<>();
            map.put(queryTerm,post);
            AbstractHit hit=new Hit(post.getDocId(),index.getDocName(post.getDocId()),map);
            hit.setScore(sorter.score(hit));
            hitList.add(hit);
        }
        sorter.sort(hitList);
        return hitList.toArray(new AbstractHit[0]);
    }

    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        AbstractPostingList postingList1=index.search(queryTerm1);
        AbstractPostingList postingList2=index.search(queryTerm2);
        List<AbstractHit> hitList1=new ArrayList<>();
        //任意一个检索词出现在命中文档
        if(combine == LogicalCombination.OR){
            int i=0,j=0;
            boolean flag1=true,flag2=true;
            int l1,l2;
            if(postingList1!=null) l1 = postingList1.size(); else {l1=0;flag1=false;}
            if(postingList2!=null) l2 = postingList2.size(); else {l2=0;flag2=false;}
            while(i<l1&&j<l2){

                AbstractPosting posting1;
                AbstractPosting posting2;
                if(flag1) posting1=postingList1.get(i); else posting1=null;
                if(flag2) posting2=postingList2.get(j); else posting2=null;
                AbstractPosting post;
                Map<AbstractTerm,AbstractPosting> map=new HashMap<>();

                //当两个文档相同时，将两个检索词和对应文档都加入到命中文档
                int id1=-1,id2=-1;
                if(flag1) id1 = posting1.getDocId();
                if(flag2) id2 = posting2.getDocId();
                if(id1==id2 && (id1!=-1 && id2!=-1)){
                    post=posting1;
                    map.put(queryTerm1,posting1);
                    map.put(queryTerm2,posting2);
                    i++;
                    j++;
                }

                //将文档ID小的那个先加入
                else if(id1<id2){
                    post=posting1;
                    map.put(queryTerm1,posting1);
                    i++;
                }
                else{
                    post=posting2;
                    map.put(queryTerm2,posting2);
                    j++;
                }
                if(post != null){
                    AbstractHit hit=new Hit(post.getDocId(),index.getDocName(post.getDocId()),map);
                    hit.setScore(sorter.score(hit));
                    hitList1.add(hit);
                }
            }

            //将余下的文档加入到命中文档
            while(i<l1){
                AbstractPosting post=postingList1.get(i);
                Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                map.put(queryTerm1,post);
                AbstractHit hit=new Hit(post.getDocId(),index.getDocName(post.getDocId()),map);
                hit.setScore(sorter.score(hit));
                hitList1.add(hit);
                i++;
            }
            while(j<l2){
                AbstractPosting post=postingList2.get(j);
                Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                map.put(queryTerm2,post);
                AbstractHit hit=new Hit(post.getDocId(),index.getDocName(post.getDocId()),map);
                hit.setScore(sorter.score(hit));
                hitList1.add(hit);
                j++;
            }
        }

        //两个检索词都必须同时出现在文档中
        else{
            int i=0,j=0;
            boolean flag1=true,flag2=true;
            int l1,l2;
            if(postingList1!=null) l1 = postingList1.size(); else {l1=0;flag1=false;}
            if(postingList2!=null) l2 = postingList2.size(); else {l2=0;flag2=false;}
            while(i<l1 && j<l2){
                AbstractPosting posting1=postingList1.get(i);
                AbstractPosting posting2=postingList2.get(j);
                if(posting1.getDocId()==posting2.getDocId()){
                    Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                    map.put(queryTerm1,posting1);
                    map.put(queryTerm2,posting2);
                    AbstractHit hit=new Hit(posting1.getDocId(),index.getDocName(posting1.getDocId()),map);
                    hit.setScore(sorter.score(hit));
                    hitList1.add(hit);
                    i++;
                    j++;
                }
                else if(posting1.getDocId()<posting2.getDocId()) i++;
                else j++;
            }
        }
        sorter.sort(hitList1);
        return hitList1.toArray(new AbstractHit[0]);
    }
    /**
     * 根据短语进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter){
        AbstractPostingList postingList1=index.search(queryTerm1);
        AbstractPostingList postingList2=index.search(queryTerm2);
        List<AbstractHit> hitList1=new ArrayList<>();
        List<AbstractHit> hitList2=new ArrayList<>();
        int i=0,j=0;
        boolean flag1=true,flag2=true;
        int l1,l2;
        if(postingList1!=null) l1 = postingList1.size(); else {l1=0;flag1=false;}
        if(postingList2!=null) l2 = postingList2.size(); else {l2=0;flag2=false;}
        while(i<l1 && j<l2){
            AbstractPosting posting1=postingList1.get(i);
            AbstractPosting posting2=postingList2.get(j);
            //当两个文档相同时，检查两个posting是否记录相邻位置的单词
            if(posting1.getDocId()==posting2.getDocId() ){
                int m=0,n=0;
                while(m<posting1.getPositions().size() && n<posting2.getPositions().size()){
                    if(posting1.getPositions().get(m)+1 == posting2.getPositions().get(n)){
                        Map<AbstractTerm,AbstractPosting> map=new HashMap<>();
                        map.put(queryTerm1,posting1);
                        map.put(queryTerm2,posting2);
                        AbstractHit hit=new Hit(posting1.getDocId(),index.getDocName(posting1.getDocId()),map);
                        hit.setScore(sorter.score(hit));
                        hitList1.add(hit);
                        m++;
                        n++;
                    }
                    else if(posting1.getPositions().get(m)+1 < posting2.getPositions().get(n)) m++;
                    else n++;
                }
                i++;
                j++;
            }
            else if(posting1.getDocId()<posting2.getDocId()) i++;
            else j++;
        }
        sorter.sort(hitList1);
        return hitList1.toArray(new AbstractHit[0]);
    }
}
