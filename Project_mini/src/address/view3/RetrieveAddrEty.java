package address.view3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;


public class RetrieveAddrEty {
	
	DBConnectionMgr 	dbMgr 	= new DBConnectionMgr();
	Connection 			con 	= null; // 연결통로
	PreparedStatement 	pstmt 	= null; // DML구문 전달하고 오라클에게 요청
	ResultSet 			rs		= null; // 조회경우 커서를 조작 필요 
	
	/***************************************************************************
	 * 회원정보 중 상세보기 구현 - 1건 조회
	 * SELECT name, address, telephone, DECODE(gender,'1','남','여')
             , relationship, birthday, comments, registedate, id 
         FROM mkaddrtb
        WHERE id = ?
	 * @param vo : 사용자가 선택한 값
	 * @return AddressVO : 조회 결과 1건을 담음
	 **************************************************************************/
	public AddressVO retrieve(AddressVO vo) {
		System.out.println("RetrieveAddrEty retrieve(AddressVO vo) 호출 성공");
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT name, address, telephone, DECODE(gender,'1','남','여') ");
		sql.append("        , relationship, birthday, comments, registedate, id   ");
		sql.append("  FROM mkaddrtb                                               ");
		sql.append(" WHERE id = ?                                                 "); 
		
		AddressVO rVO = null;
		int id = vo.getId();
		
		try {
			con = dbMgr.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery(); ////////////// select문, 결과집합을 가져올 때는 executeQuery 사용 / insert,Update,Delete 등의 조작을 할 때는 executeUpdate 사용
			////////// 커서를 움직일 수 있는 도구인 rs가 next()문을 통해 다음 레코드를 가리키고 get메소드로 값을 요청한다.
			if(rs.next()) { 
				rVO = new AddressVO();
				rVO.setName(rs.getString(1));
				rVO.setAddress(rs.getString(2));
				rVO.setTelephone(rs.getString(3));
				rVO.setGender(rs.getString(4));
				rVO.setRelationship(rs.getString(5));
				rVO.setBirthday(rs.getString(6));
				rVO.setComments(rs.getString(7));
				rVO.setRegistedate(rs.getString(8));
				rVO.setId(rs.getInt(9));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con);
		}
		
		return rVO;
	}
	
	
	
	/***************************************************************************
	 * 회원 목록 전체 조회 구현(새로고침 시 재사용) - n건 조회
	 * SELECT name, address, telephone, DECODE(gender,'1','남','여')
              , relationship, birthday, comments, registedate, id 
         FROM mkaddrtb
	 * @return AddressVO[] : 조회 결과 n건을 담음
	 **************************************************************************/
	public AddressVO[] retrieve() {
		System.out.println("RetrieveAddrEty retrieve() 호출 성공");		
		AddressVO[] vos = null;
		Vector<AddressVO> vec = new Vector<>( ); // AddressVO 타입의 Vector 생성(초기화값 생략())
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT name, address, telephone, DECODE(gender,'1','남','여') AS gender ");
		sql.append("        , relationship, birthday, comments, registedate, id             ");
		sql.append("  FROM mkaddrtb                                                         ");
		sql.append("  ORDER BY id desc                                             			");
		
		AddressVO vo = null;
		
		try {
			con = dbMgr.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				vo = new AddressVO();
				vo.setName(rs.getString("name"));
				vo.setAddress(rs.getString("address"));
				vo.setTelephone(rs.getString("telephone"));
				vo.setGender(rs.getString("gender"));
				vo.setRelationship(rs.getString("relationship"));
				vo.setBirthday(rs.getString("birthday"));
				vo.setComments(rs.getString("comments"));
				vo.setRegistedate(rs.getString("registedate"));
				vo.setId(rs.getInt("id"));// 커서를 움직여서 가져온 오라클 데이터를 AddressVO의 메소드를 통해 값 세팅
				vec.add(vo); // AddressVO vo에 담긴 값을 Vector에 추가한다.
				
				
			}
			
			vos = new AddressVO[vec.size()]; // Vector의 데이터 개수를 알 수 있는 메소드를 통해 배열 생성
			vec.copyInto(vos); // copyInto() 메소드를 통해 배열로 복사(변환)
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionMgr.freeConnection(rs, pstmt, con);
		}
		return vos;
	}////////////////////////////////////// end of public AddressVO[] retrieve()
	
	
}

/*
ID,NAME,ADDRESS,TELEPHONE,GENDER,RELATIONSHIP,BIRTHDAY,COMMENTS,REGISTEDATE
3,나초보,서울시 마포구 공덕동,025556968,2,2,19801215,주연테크,REGISTEDATE
1,홍길동,서울시 영등포구 당산동,111,1,동창,19901010,222,20081117
2,이순신,서울시 서초구 양재동,222,2,회사동료,19901110,333,20100113
4,강감찬,경기도 광명시,11,1,동창,19801120,테스트합니다.,2011-03-20
9,나초조,4455,44455,1,44555,19800702,자영업,20110320
7,223,223,223,2,223,223,223,20190609
10,조조,경기도 화성시,44455,1,44555,19800702,자영업,20110320
*/