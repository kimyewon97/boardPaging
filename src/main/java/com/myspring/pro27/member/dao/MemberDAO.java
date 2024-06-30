package com.myspring.pro27.member.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.myspring.pro27.member.vo.MemberVO;

public interface MemberDAO {
	 public List selectAllMemberList() throws DataAccessException;
	 public int insertMember(MemberVO memberVO) throws DataAccessException ;
	 public int deleteMember(String id) throws DataAccessException;
	 public MemberVO loginById(MemberVO memberVO) throws DataAccessException;
	 
	//추가
	public MemberVO findMember(String id) throws DataAccessException; //회원 수정하기전 조회
	public int modMember(MemberVO memberVO) throws DataAccessException; //회원 수정 후 반영

}
