package de.htwkleipzig.mmdb.mvc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.htwkleipzig.mmdb.model.Author;
import de.htwkleipzig.mmdb.model.Paper;
import de.htwkleipzig.mmdb.model.University;
import de.htwkleipzig.mmdb.service.AuthorService;
import de.htwkleipzig.mmdb.service.PaperService;
import de.htwkleipzig.mmdb.service.UniversityService;
import de.htwkleipzig.mmdb.util.AuthorHelper;
import de.htwkleipzig.mmdb.util.PaperHelper;
import de.htwkleipzig.mmdb.util.UniversityHelper;

/**
 * @author men0x
 * 
 */
@Controller
public class SearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private PaperService paperService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private UniversityService universityService;

    @RequestMapping(value = "/search")
    public String elasticsearch(@RequestParam(required = true) String searchPhrase, Model model) {
        // search at QuizRDF
        LOGGER.info("search for a query");
        if (searchPhrase.isEmpty()) {
            LOGGER.debug("searchPhrase is empty");
            return "home";
        }
        QueryStringQueryBuilder query = QueryBuilders.queryString(searchPhrase).allowLeadingWildcard(false)
                .useDisMax(true);
        SearchResponse response = paperService.search(query);
        LOGGER.info("total hits {}", response.getHits().getTotalHits());
        LOGGER.info("MaxScore {}", response.getHits().getMaxScore());
        model.addAttribute("totalHits", response.getHits().getTotalHits());
        model.addAttribute("maxScore", response.getHits().getMaxScore());
        List<Paper> papers = new ArrayList<Paper>();
        for (SearchHit hit : response.getHits().getHits()) {
            if (hit.isSourceEmpty()) {
                LOGGER.info("source is empty");
            }
            LOGGER.info("id of the document {}", hit.getId());
            LOGGER.info("score of the hit {}", hit.getScore());
            model.addAttribute("documentId", hit.getId());
            model.addAttribute("documentScore", hit.getScore());

            Map<String, Object> resultMap = hit.sourceAsMap();

            Paper paper = PaperHelper.source2Paper(resultMap);

            paper.setContent("");
            LOGGER.debug("paper: {}", paper.getPaperId());
            papers.add(paper);

        }
        // LOGGER.info("MaxScore {}", response.getHits().getHits());
        model.addAttribute("paper", papers);
        model.addAttribute("searchTerm", searchPhrase);
        return "resultPage";
    }

    @RequestMapping(value = "/extendedSearch")
    public String extendedSearch() {
        LOGGER.info("starting extended search");
        return "extendedSearch";
    }

    /**
     * all fields can contain more than one value. They have seperated by spaces
     * 
     * @param author
     *            the authors query.should contain
     * @param uni
     *            query.should contain
     * @param category
     *            query.must contain
     * @param tags
     *            query.must contain
     * @param and
     *            query.must contain
     * @param or
     *            query.should contain
     * @param secialand
     *            query.must contain
     * @param model
     *            the container for the result page
     * @return the String the resultpage is named
     */
    @RequestMapping(value = "/evaluateExtendedSearch")
    public String evaluateExtendedSearch(@RequestParam String author, @RequestParam String uni,
            @RequestParam String category, @RequestParam String tags, @RequestParam String and,
            @RequestParam String or, @RequestParam String secialand, Model model) {
        LOGGER.info("starting evaluating of extended Search");
        model.addAttribute("searchTerm", and);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (!and.isEmpty()) {
            boolQuery.must(andQuery(and));
        }
        if (!or.isEmpty()) {
            boolQuery.should(orQuery(or));
        }
        if (!author.isEmpty()) {
            boolQuery.should(authorQuery(author));

        }
        if (!uni.isEmpty()) {
            boolQuery.should(uniQuery(uni));

        }
        if (!category.isEmpty()) {
            boolQuery.must(categoryQuery(category));

        }
        if (!tags.isEmpty()) {
            boolQuery.must(tagsQuery(tags));

        }
        if (!secialand.isEmpty()) {
            boolQuery.must(secialandQuery(secialand));

        }

        prepareResult(model, paperService.search(boolQuery));
        return "resultPage";
    }

    /**
     * uni
     * 
     * @param uni
     * @return
     */
    private QueryBuilder uniQuery(String uni) {
        LOGGER.debug("University Search");
        // TODO - Author field is an id that point to the author in the author index
        // have to search the author in index author, get the id and have to search the authorId in field author of
        // index paper IMPORTANT the authorsId is an array!!
        QueryBuilder universityDocQuery = null;
        QueryBuilder universityQuery = QueryBuilders.queryString(uni).useDisMax(true)
                .defaultOperator(QueryStringQueryBuilder.Operator.OR).analyzer("simple").field("name");
        LOGGER.debug("universityQuery: {}", universityQuery.toString());

        SearchResponse universityResponse = universityService.search(universityQuery);
        List<String> universityIds = new ArrayList<String>();
        for (SearchHit hit : universityResponse.getHits().getHits()) {
            if (hit.isSourceEmpty()) {
                LOGGER.info("source is empty");
            }
            LOGGER.info("id of the university {}", hit.getId());
            LOGGER.info("score of the hit {}", hit.getScore());

            Map<String, Object> resultMap = hit.sourceAsMap();

            University universityObject = UniversityHelper.source2University(resultMap);

            LOGGER.debug("university: {}", universityObject.toString());
            LOGGER.debug("universityIds: {}", universityObject.getUniversityId());
            universityIds.add(universityObject.getUniversityId());

        }
        universityDocQuery = QueryBuilders.inQuery("universityIds", universityIds.toArray());
        LOGGER.debug("query with universityIds for documentSearch {}", universityDocQuery.toString());

        return universityDocQuery;
    }

    /**
     * Auhor search at authors (or concat), extract the paper ids and return a query for the search at the paperids
     * 
     * @param author
     * @return the Query with the paperIds
     */
    private QueryBuilder authorQuery(String author) {
        LOGGER.debug("Author Search");
        // TODO - Author field is an id that point to the author in the author index
        // have to search the author in index author, get the id and have to search the authorId in field author of
        // index paper IMPORTANT the authorsId is an array!!
        QueryBuilder authorDocQuery = null;
        QueryBuilder authorQuery = QueryBuilders.queryString(author).useDisMax(true)
                .defaultOperator(QueryStringQueryBuilder.Operator.OR).analyzer("simple").field("lastName")
                .field("name");
        LOGGER.debug("authorQuery: {}", authorQuery.toString());

        SearchResponse authorResponse = authorService.search(authorQuery);
        List<String> paperIds = new ArrayList<String>();
        for (SearchHit hit : authorResponse.getHits().getHits()) {
            if (hit.isSourceEmpty()) {
                LOGGER.info("source is empty");
            }
            LOGGER.info("id of the author {}", hit.getId());
            LOGGER.info("score of the hit {}", hit.getScore());

            Map<String, Object> resultMap = hit.sourceAsMap();

            Author authorObject = AuthorHelper.source2author(resultMap);

            LOGGER.debug("author: {}", authorObject.toString());
            LOGGER.debug("paper: {}", authorObject.getPaperIds().toString());
            paperIds.addAll(authorObject.getPaperIds());

        }
        authorDocQuery = QueryBuilders.inQuery("paperId", paperIds.toArray());
        LOGGER.debug("query with authorIds for documentSearch {}", authorDocQuery.toString());

        return authorDocQuery;
    }

    /**
     * tags aka keywords
     * 
     * @param tags
     * @return
     */
    private QueryBuilder tagsQuery(String tags) {
        LOGGER.debug("tagsQuery build");
        tags = tags.replace("+", " ");
        QueryBuilder tagsQuery = QueryBuilders.queryString(tags).defaultOperator(Operator.OR).field("keywords");
        LOGGER.debug("query build: {}", tagsQuery.toString());
        return tagsQuery;
    }

    /**
     * category aka kindOf
     * 
     * @param category
     * @return
     */
    private QueryBuilder categoryQuery(String category) {
        LOGGER.debug("categoryQuery build");
        category = category.replace("+", " ");
        QueryBuilder categoryQuery = QueryBuilders.queryString(category).defaultOperator(Operator.OR).field("kindOf");
        LOGGER.debug("query build: {}", categoryQuery.toString());
        return categoryQuery;
    }

    /**
     * secialand
     * 
     * @param secialand
     * @return
     */
    private QueryBuilder secialandQuery(String secialand) {
        LOGGER.debug("secialand query builder");
        secialand = secialand.replace("+", " ");
        QueryBuilder secialandQuery = QueryBuilders.queryString(secialand).defaultOperator(Operator.OR).field("title")
                .field("content");
        LOGGER.debug("query build: {}", secialandQuery.toString());
        return secialandQuery;

    }

    /**
     * OR
     * 
     * @param or
     * @return the or query
     */
    private QueryBuilder orQuery(String or) {
        LOGGER.debug("or is not empty");
        or = or.replace("+", " ");
        QueryBuilder orQuery = QueryBuilders.queryString(or).defaultOperator(Operator.OR).field("title")
                .field("content");
        LOGGER.debug("query build: {}", orQuery.toString());
        return orQuery;
    }

    /**
     * and
     * 
     * @param and
     * @return
     */
    private QueryBuilder andQuery(String and) {
        LOGGER.debug("and is not empty");
        and = and.replace("+", " ");
        QueryBuilder andQuery = QueryBuilders.queryString(and).defaultOperator(Operator.AND).field("title")
                .field("content");
        LOGGER.debug("query build: {}", andQuery.toString());
        return andQuery;
    }

    /**
     * @param model
     * @param boolQuery
     */
    private void prepareResult(Model model, SearchResponse response) {
        // put all the subqueries together
        LOGGER.info("result preparation");
        LOGGER.info("total hits {}", response.getHits().getTotalHits());
        LOGGER.info("MaxScore {}", response.getHits().getMaxScore());
        model.addAttribute("totalHits", response.getHits().getTotalHits());
        model.addAttribute("maxScore", response.getHits().getMaxScore());
        List<Paper> papers = new ArrayList<Paper>();
        for (SearchHit hit : response.getHits().getHits()) {
            if (hit.isSourceEmpty()) {
                LOGGER.info("source is empty");
            }
            LOGGER.info("id of the document {}", hit.getId());
            LOGGER.info("score of the hit {}", hit.getScore());
            model.addAttribute("documentId", hit.getId());
            model.addAttribute("documentScore", hit.getScore());

            Map<String, Object> resultMap = hit.sourceAsMap();

            Paper paper = PaperHelper.source2Paper(resultMap);

            paper.setContent("");
            LOGGER.debug("paper: {}", paper.getPaperId());
            papers.add(paper);

        }
        // LOGGER.info("MaxScore {}", response.getHits().getHits());
        model.addAttribute("paper", papers);
    }

}
