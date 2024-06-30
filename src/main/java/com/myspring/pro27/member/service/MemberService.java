package com.myspring.pro27.member.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.myspring.pro27.member.vo.MemberVO;

public interface MemberService {
	 public List listMembers() throws DataAccessException;
	 public int addMember(MemberVO memberVO) throws DataAccessException;
	 public int removeMember(String id) throws DataAccessException;
	 public MemberVO login(MemberVO memberVO) throws Exception;
	 
	 //추가 회원 정보
	 public MemberVO findMember(String id) throws DataAccessException;
	 
	 //추가 회원 수정
	 public int modMember(MemberVO memberVO) throws DataAccessException;
}
