package com.study.springrest.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.study.springrest.domain.BoardVO;
import com.study.springrest.domain.Criteria;
import com.study.springrest.domain.PageMaker;
import com.study.springrest.domain.ReplyVO;
import com.study.springrest.service.BoardService;
import com.study.springrest.service.ReplyService;

/**
 * Handles requests for the application home page.
 */
@RestController
@RequestMapping(value = "/reply")
public class Reply {
	
	private static final Logger logger = LoggerFactory.getLogger(Reply.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@Inject
	private ReplyService service;
	
	@RequestMapping(value = "/{board_no}", method = RequestMethod.GET)
	public ResponseEntity<List<ReplyVO>> list(@PathVariable("board_no") Integer board_no) {
		ResponseEntity<List<ReplyVO>> entity = null;
		try{
			entity = new ResponseEntity<>(service.getList(board_no), HttpStatus.OK);
		}catch(Exception e) {
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	// /유닛/ R 목록
	@RequestMapping(value = "/{board_no}/{page}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> listPage(@PathVariable("board_no") Integer board_no, @PathVariable("page") Integer page) {
		
		ResponseEntity<Map<String, Object>> entity = null;
		
		try{
			//페이지 처리를 위한 Criteria 생성
			Criteria criteria = new Criteria();
			criteria.setPage(page);
			
			//덧글 개수 가져오기
			int replyTotal = service.getTotlaCount(board_no);
			//덧글 가져오기
			List<ReplyVO> list = service.getListPage(board_no, criteria);
			
			//페이지 메이커에 총 덧글 개수와, 현재 페이지가 있는 criteria넘겨서 페이징값을 계산한다. 
			PageMaker pm=new PageMaker(criteria, replyTotal);
			
			//최종적으로 Map에 덧글 목록과 페이징 처리값을 담아서
			Map<String, Object> map = new HashMap<>();
			map.put("l", list);
			map.put("p", pm);
			
			entity = new ResponseEntity<>(map, HttpStatus.OK); //기본적으로 2개인자 필요
		} catch(Exception e){
			e.printStackTrace();
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	
	// /유닛/   method=post
	@RequestMapping(value = "/{board_no}", method = RequestMethod.POST)
	public ResponseEntity<String> insert(@RequestBody ReplyVO vo, @PathVariable("board_no") Integer board_no) {
		ResponseEntity<String> entity = null;
		try{
			vo.setBoard_no(board_no);
			service.insert(vo);
			entity = new ResponseEntity<>("SUCCESS", HttpStatus.OK);
		}catch(Exception e) {
			entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	@RequestMapping(value = "/{reply_no}", method = RequestMethod.DELETE)
	public ResponseEntity<String> delete(@PathVariable("reply_no") Integer reply_no) {
		ResponseEntity<String> entity = null;
		try{
			service.delete(reply_no);
			entity = new ResponseEntity<>("SUCCESS", HttpStatus.OK);
		}catch(Exception e) {
			entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
}
