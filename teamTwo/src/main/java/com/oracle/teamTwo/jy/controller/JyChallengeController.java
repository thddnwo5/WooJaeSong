package com.oracle.teamTwo.jy.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.oracle.teamTwo.jy.service.JyChallengeService;
import com.oracle.teamTwo.jy.model.ChgChatBox;
import com.oracle.teamTwo.jy.model.ChgReviewBox;
import com.oracle.teamTwo.jy.model.JyChg_photochk;
import com.oracle.teamTwo.teamtwo.model.Challenge;
import com.oracle.teamTwo.teamtwo.model.Chg_chat;
import com.oracle.teamTwo.teamtwo.model.Chg_photochk;
import com.oracle.teamTwo.teamtwo.model.Chg_review;
import com.oracle.teamTwo.jy.service.Paging;
import com.oracle.teamTwo.dh.service.dhUserServiceImpl;
import com.oracle.teamTwo.jy.model.CheckMyState;
import com.oracle.teamTwo.teamtwo.model.Hashtag2;
import com.oracle.teamTwo.teamtwo.model.Part_challenge;
import com.oracle.teamTwo.teamtwo.model.Point;
import com.oracle.teamTwo.teamtwo.model.User_info;

@Controller
public class JyChallengeController {
	private static final Logger logger = LoggerFactory.getLogger(JyChallengeController.class);
	
	@Autowired
	private JyChallengeService jcs;
	
	// teamtwo user model 끝
	@Autowired
	dhUserServiceImpl dus;

	// teamtwo user model 시작
	   @SuppressWarnings("null")
	   @ModelAttribute("user_model")
	   public void user_model(User_info user_model, HttpServletRequest request) {
		   
		   HttpSession session = request.getSession();
		   
			   session.setAttribute("user_id_email", session.getAttribute("user_id_email"));
			   session.setAttribute("user_nickname", session.getAttribute("user_nickname"));
			   session.setAttribute("user_image", session.getAttribute("user_image"));
		   
		   System.out.println(user_model.getUser_image());

	   } // teamtwo user model 끝
	
	//챌린지 리스트 조회하기
	@RequestMapping(value="listChallenge")
	public String list(Challenge challenge, JyChg_photochk jpk, String currentPage, Model model) {
		System.out.println("JyChallenge Controller list Start...");
		int total = jcs.total();
		System.out.println("JyChallenge Controller list total-->"+total);
		
		List<Challenge> listChallenge = jcs.listChallenge(challenge);
		model.addAttribute("listChallenge", listChallenge);
		model.addAttribute("total", total);
		System.out.println("listChallenge-->"+listChallenge);
		return "jy/listChallenge";
	}
	
	//챌린지 상세페이지로 출발!
	@GetMapping(value="challengeDetail")
	public String challengeDetail(HttpServletRequest request, JyChg_photochk jpk, String currentPage, Model model) {
		System.out.println("challengeDetail Start...");
		
		int chg_num = Integer.parseInt(request.getParameter("chg_num")); 
		
		HttpSession session = request.getSession();
		String user_id_email = (String) session.getAttribute("user_id_email");
		String user_nickname = (String) session.getAttribute("user_nickname");
		String user_image = (String) session.getAttribute("user_image");
		
		System.out.println("상세페이지 chg_num--->"+chg_num);
		System.out.println("상세페이지 user_id_email--->"+user_id_email);
		System.out.println("상세페이지 user_nickname--->"+user_nickname);
		System.out.println("상세페이지 user_image--->"+user_image);
		
		
		int chg_state = jcs.chg_state(chg_num); //챌린지상태
		System.out.println("상세페이지 chg_state..."+chg_state);
		
		List<Challenge> chall = jcs.chall(chg_num); //챌린지 값
		List<Hashtag2> ht2 = jcs.ht2(chg_num); //해시태그 값
		User_info user = jcs.user(chg_num); //챌린지 마스터 프사,닉네임 값
		int totpart = jcs.totpart(chg_num); //현재 참여인원
		
		int totpart1 = totpart+1;
		
		Map<String, Object> updateTotpart = new HashMap<String, Object>();
		updateTotpart.put("chg_num", chg_num);
		updateTotpart.put("totpart1", totpart1);
		int updateTot = jcs.updateTot(updateTotpart);
		
		List<User_info> userpart = jcs.userpart(chg_num); //현재 참여인원 프사,닉네임 값
		int point = jcs.point(user_id_email); //포인트 리스트 가져오기
		List<ChgChatBox> chgChatBox = jcs.showChat(chg_num); //경민 소통하기
	    model.addAttribute("chgChatBox",chgChatBox);
	     
	    List<ChgReviewBox> chgReview = jcs.showReview(chg_num); //경민 후기
	    model.addAttribute("chgReviewBox",chgReview);
		
		//이 챌린지에 내가 있는지 없는지 상태체크 (참여1, 미참여0)
		CheckMyState cms = new CheckMyState();
		cms.setChg_num(chg_num);
		cms.setUser_id_email(user_id_email);
		int myChgState = jcs.checkMyState(cms); //2개이상 보내야할때, 객체에 담아 보내기
		
		String masterid = chall.get(0).getChg_master(); // 마스터 아이디 가져오기
		
		
		// 도토리바
		// CheckMyState cms 에 사용자 아이디랑 chg_num 들어있음
	      Part_challenge pc = jcs.myChgBar(cms);
	      double confirmCount = 0;
	      double chgDays = 0;
	      if (pc == null) {
	         confirmCount = 0;
	         chgDays = 0;
	         model.addAttribute("myPercent", confirmCount);
	      } else {
	         confirmCount = pc.getConfirm_count();
	         chgDays = pc.getChg_days();

	         if (confirmCount == 0) {
	            model.addAttribute("myPercent", confirmCount);
	         } else {
	            double myPercent = (confirmCount / chgDays) * 100;
	            model.addAttribute("myPercent", myPercent);
	         }

	      }
	      Date today = new Date();
	      Date chgStartDay =null;
	      if(pc == null) {
	         chgStartDay =null;
	         model.addAttribute("chgPercent", 100);
	      }else {
	         chgStartDay = chall.get(0).getChg_startdate();
	         long chgProcessDay = today.getTime() - chgStartDay.getTime();
	         double days = (double) ((chgProcessDay) / (24 * 60 * 60 * 1000));
	         if (chgProcessDay >= 0 && days <= chgDays) {
	            double chgPercent = ((days + 1) / chgDays) * 100;
	            model.addAttribute("chgPercent", chgPercent);
	         } else if (days > chgDays) {
	            model.addAttribute("chgPercent", 100);
	         }
	      }

		// 랜덤문구
		String today_phrase = jcs.todayPhrase();
		model.addAttribute("today_phrase", today_phrase);
		
		//오늘 인증했는지 안했는지!
		SimpleDateFormat cut = new SimpleDateFormat("yyyy-MM-dd");
		String cutToday = cut.format(today); //Date today = new Date(); 경민씨가 위에서 선언해줌.
		System.out.println(cutToday);
		
		JyChg_photochk jcpk = new JyChg_photochk();
		jcpk.setChg_num(chg_num);
		jcpk.setParti_id_email(user_id_email);
		jcpk.setToday(cutToday);
		
		int todayCnt = jcs.checkToday(jcpk);
		model.addAttribute("todayCnt", todayCnt);
		
		//소통하기
		int countChat = jcs.countChat(chg_num);
		model.addAttribute("countChat", countChat);
		
		// 인증사진 리스트
		int total = jcs.totPhoto(chg_num);
		System.out.println("인증사진리스트 갯수 컨트롤러 도착-->" + total);
		Paging pg = new Paging(total, currentPage);
		jpk.setStart(pg.getStart()); // 시작시1
		jpk.setEnd(pg.getEnd()); // 시작시10
		
		int start = pg.getStart();
		int end = pg.getEnd();

		Map<String, Object> chgmap = new HashMap<String, Object>();
		chgmap.put("chg_num", chg_num);
		chgmap.put("start", start);
		chgmap.put("end", end);
		List<JyChg_photochk> photochk = jcs.photochk(chgmap);
		
		model.addAttribute("photochk", photochk);
		model.addAttribute("pg", pg);
		model.addAttribute("totalCnt", total);
		
		//저장소 ㅋㅋ
		model.addAttribute("chg_num", chg_num); //챌린지 번호
		model.addAttribute("user_id_email",user_id_email); //아이디 값 저장
		model.addAttribute("user_nickname", user_nickname); //닉네임 값 저장
		model.addAttribute("user_image", user_image); // 유저 이미지 저장
		model.addAttribute("point_balance", point); //포인트
		model.addAttribute("chg_num", chg_num); 
		model.addAttribute("myChgState", myChgState); //챌린지 참여여부 저장
		model.addAttribute("masterid", masterid); //마스터 아이디 값 저장
		model.addAttribute("chg_state",chg_state); //챌린지 상태 저장
		model.addAttribute("chall",chall); //챌린지값 저장
		model.addAttribute("ht2", ht2); //해시태그 값 저장
		model.addAttribute("user", user); //챌린지 마스터 프사,닉네임 값 저장
		model.addAttribute("totpart", totpart); //현재 참여인원 저장
		model.addAttribute("userpart", userpart); //현재 참여인원 프사,닉네임 값 저장

		return "jy/challDetailGather";

	}
	
	@ResponseBody //아작스 쓸 경우 작성
	@RequestMapping(value="listConfirm")
	public Map<String, Object> listConfirm(int chg_num, JyChg_photochk jpk, String currentPage, Model model) {
		// 인증사진 리스트
					int total = jcs.totPhoto(chg_num);
					System.out.println("인증사진리스트 갯수 컨트롤러 도착-->" + total);
					Paging pg = new Paging(total, currentPage);
					jpk.setStart(pg.getStart()); // 시작시1
					jpk.setEnd(pg.getEnd()); // 시작시10
					
					int start = pg.getStart();
					int end = pg.getEnd();

					Map<String, Object> chgmap = new HashMap<String, Object>();
					chgmap.put("chg_num", chg_num);
					chgmap.put("start", start);
					chgmap.put("end", end);
					List<JyChg_photochk> photochk = jcs.photochk(chgmap);		
					System.out.println("photochk.size()-->"+photochk.size());
					
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("photochk", photochk);
					result.put("pg", pg);
					result.put("totalCnt", total); 
					
		
		return result;
	}
	
	@ResponseBody //아작스 쓸 경우 작성
	@RequestMapping(value="challTimeChk")
	public Challenge timeChk(int chg_num, Model model) {
		Challenge chg = new Challenge();
		
		chg = jcs.timeChk(chg_num);
		System.out.println("challengeDetail Start timeChk..."+chg_num);
		
		return chg;
	}
	
	//경민 챌린지 소통하기
	@GetMapping("insertChat")
	public String insertChat(Chg_chat chgChat, Model model) {
		
		
		System.out.println("chgChat.getChg_num()"+chgChat.getChg_num());
		int chk = jcs.insertChat(chgChat);
		
		model.addAttribute("chg_num", chgChat.getChg_num());
		
		return "forward:challengeDetail";
	}
	
	//챌린지 참가 신청서 - 배팅금액/신청여부 저장
	@PostMapping("insertChallJoinBatt")
	public String insertChallJoinBatt(HttpServletRequest request, Point point, Part_challenge part_challenge, Model model) {
		System.out.println("point.getPoint_minus()-->" + point.getPoint_minus());
		System.out.println("part_challenge.getChg_days()-->" + part_challenge.getChg_days());
		int resultPoint = jcs.insertPoint(point);
		System.out.println("resultPoint-->" + resultPoint);
		model.addAttribute("resultPoint", resultPoint);
		

		int resultPart = jcs.insertPart(part_challenge);
		System.out.println("resultPart-->" + resultPart);
		model.addAttribute("resultPart", resultPart);
		
		int chg_num = part_challenge.getChg_num();
		HttpSession session = request.getSession();
		String user_id_email = (String) session.getAttribute("user_id_email");
		String user_nickname = (String) session.getAttribute("user_nickname");
		String user_image = (String) session.getAttribute("user_image");
		
		model.addAttribute("chg_num", chg_num); //챌린지 번호
		model.addAttribute("user_id_email",user_id_email); //아이디 값 저장
		model.addAttribute("user_nickname", user_nickname); //닉네임 값 저장
		model.addAttribute("user_image", user_image); // 유저 이미지 저장

		return "jy/insertChallJoinBattResult";
	}
	
	//경민 챌린지 후기
	@GetMapping("updateReview")
	public String updateReview(Chg_review chg_review, Model model) {

		jcs.updateReview(chg_review);

		model.addAttribute("user_id_email", chg_review.getUser_id_email());
		model.addAttribute("chg_num", chg_review.getChg_num());

		return "forward:challengeDetail";

	}
	
	//인증게시판 사진저장
	@PostMapping(value="confirmPicSave")
	public String uploadForm(HttpServletRequest request, MultipartFile file1, Model model) throws Exception {
		String uploadPath = request.getSession().getServletContext().getRealPath("upload/confirmPic"); //경로
		System.out.println("uploadForm POST Start");
		logger.info("originalName: "+file1.getOriginalFilename()); //원래 파일이름
		logger.info("size: "+file1.getSize()); // 파일크기
		logger.info("bytes: "+file1.getBytes()); // 파일크기
		logger.info("contentType: "+file1.getContentType()); //image/png
		
		int chg_num = Integer.parseInt(request.getParameter("chg_num")); 
		String user_id_email = request.getParameter("parti_id_email");
		String user_nickname = request.getParameter("user_nickname");
		String user_image = request.getParameter("user_image");
		
		System.out.println("인증게시판 사진저장 chg_num-->"+chg_num);
		System.out.println("인증게시판 사진저장 user_id_email-->"+user_id_email);
		System.out.println("인증게시판 사진저장  user_nickname-->"+user_nickname);
		System.out.println("인증게시판 사진저장 user_image-->"+user_image);
		
		//사진이름 null, 공백으로 조건을 줘도 안되더라. 사이즈로 하자.
		long size = file1.getSize();
		
		//사진첨부없이 제출할경우
		if(size == 0) {
			
			int photoResult = 2;
			model.addAttribute("photoResult",photoResult);
	
		}else {
			
			String savedName = uploadFile(file1.getOriginalFilename(), file1.getBytes(), uploadPath); //db로 엮어서 저장
			model.addAttribute("savedName", savedName);

			//DB저장
			Chg_photochk cpk = new Chg_photochk();
			cpk.setChg_num(chg_num);
			cpk.setParti_id_email(user_id_email);
			cpk.setConfirm_image(savedName);
			
			int photoResult = jcs.cpkResult(cpk);
			model.addAttribute("photoResult",photoResult);
			
			System.out.println("인증사진 insert 성공?-->"+photoResult);
			
		}
		model.addAttribute("chg_num", chg_num); //챌린지 번호
		model.addAttribute("user_id_email",user_id_email); //아이디 값 저장
		model.addAttribute("user_nickname", user_nickname); //닉네임 값 저장
		model.addAttribute("user_image", user_image); // 유저 이미지 저장
		
		return "jy/insertConfirmPicResult";
	}

	//사진파일 유일한 이름만들어주기 	  //file1.getOriginalFilename(), file1.getBytes(), uploadPath
	private String uploadFile(String originalName, byte[] fileData, String uploadPath) throws Exception {
		UUID uid = UUID.randomUUID(); //universally unique identifier 유일한 번호를 만들어주겠다.(중첩되지 않는 번호)
		
		System.out.println("uploadPath-->"+uploadPath);
		
		//Directory 생성
		File fileDirectory = new File(uploadPath);
		
		if(!fileDirectory.exists()) { //파일경로 존재여부 확인
			fileDirectory.mkdirs(); //경로가 없는경우 만들어주기
			System.out.println("업로드용 폴더 생성: "+uploadPath);
		}
		
		String savedName = uid.toString() + "_" + originalName;

		// 파일 객체 생성 = 기본 저장경로 + 날짜경로 + UUID_파일명
		File target = new File(uploadPath, savedName);
		FileCopyUtils.copy(fileData, target); //org.springframework.util.FileCopyUtils
		
		return savedName;
	}
	
	//팝업
	@GetMapping(value="photoPop")
	public String photoPop(HttpServletRequest request, Model model) {
		
		String confirm_image = request.getParameter("confirm_image");
		model.addAttribute("confirm_image", confirm_image);
		
		return "jy/photoPop";	
	}
	
	//마스터 인증사진컨펌
	@PostMapping(value="photoConfirm")
	public String photoConfirm(HttpServletRequest request, Model model) {
		
		int chg_num = Integer.parseInt(request.getParameter("chg_num")); 
		HttpSession session = request.getSession();
		String user_id_email = (String) session.getAttribute("user_id_email");
		String user_nickname = (String) session.getAttribute("user_nickname");
		String user_image = (String) session.getAttribute("user_image");
		String confirm_state1 = request.getParameter("confirm_state1");
		int photochk_num = Integer.parseInt(request.getParameter("photochk_num")); 	
		int confirm_state = 0;

		if (confirm_state1.equals("수락")) {
			confirm_state = 1;
		} else if (confirm_state1.equals("거절")) {
			confirm_state = 2;
		}
		
		Chg_photochk cpk = new Chg_photochk();
		cpk.setConfirm_state(confirm_state);
		cpk.setPhotochk_num(photochk_num);
		int confirmEnd = jcs.insertPhotoConfirm(cpk);
		
		model.addAttribute("confirm_state", confirm_state); //수락거절여부
		model.addAttribute("confirmEnd", confirmEnd); //업데이트 성공여부
		model.addAttribute("chg_num", chg_num); // 챌린지 번호
		model.addAttribute("user_id_email", user_id_email); // 아이디 값 저장
		model.addAttribute("user_nickname", user_nickname); // 닉네임 값 저장
		model.addAttribute("user_image", user_image); // 유저 이미지 저장

		return "jy/updatePhotoConfirmResult";	
	}
	
	
	

	
}
