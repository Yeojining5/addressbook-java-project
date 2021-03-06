package address.view3;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class AddressLogin extends JFrame implements ActionListener, FocusListener  {
	/////////////////////////////////////////////////////
	/* 선언부 */
	/////////////////////////////////////////////////////
	Connection		  con	= null;
	PreparedStatement pstmt = null;
	ResultSet 		  rs	= null;
	DBConnectionMgr   dbMgr = new DBConnectionMgr();
	
	String path = "src//images//address//";
	String nickName="";
	String imgPath="src\\images\\address\\";
	//JLabel jlb_name = new JLabel("     NAME");
	//JLabel jlb_bday = new JLabel("YYYYMMDD");
	
	JLabel jlb_logo = new JLabel(); ////////////////////////////
	ImageIcon icon = new ImageIcon(path+"logo4.png");

	Font jl_font1 = new Font("dubai", Font.PLAIN, 18);
	Font jl_font2 = new Font("나눔고딕", Font.ITALIC, 13);
	
	JTextField jtf_name = new JTextField("User name (ex. 나신입)");
	JTextField jpf_bday = new JTextField("YYYYMMDD (ex. 19900712)");
	
	JButton jbtn_login = new JButton("L o g i n");
			//new ImageIcon("C:\\Users\\minkh\\Desktop\\practice\\dev_java\\src\\com\\Final\\image\\login.png"));
	JButton jbtn_join = new JButton("S i g n  u p");
			//new ImageIcon("C:\\Users\\minkh\\Desktop\\practice\\dev_java\\src\\com\\Final\\image\\co   nfirm.png"));

	// JPanel에 쓰일 이미지아이콘
	//ImageIcon ig = new ImageIcon("C:\\Users\\minkh\\Desktop\\practice\\dev_java\\src\\com\\Final\\image\\main.png");
	ImageIcon ig = new ImageIcon(imgPath+"main2.PNG");
	
	ModifyDialog mDialog = null;
	AddressBook abook = null;
	MyTableModel myTableModel = null;;
	
	/////////////////////////////////////////////////////
	/* 생성자 */
	/////////////////////////////////////////////////////
	
	public AddressLogin(){
		initDisplay();
	}
	
//	public AddressLogin(AddressBook abook) {
//		this.abook = abook;
//	}
	

	/////////////////////////////////////////////////////
	/* jpanal 오버라이드 */
	/////////////////////////////////////////////////////

	/* 배경이미지 */
	class mypanal extends JPanel {
		public void paintComponent(Graphics g) {
			g.drawImage(ig.getImage(), 0, 0, null);
			setOpaque(false);
			super.paintComponents(g);
		}
	}

	
	///////////////////////////////////////////////// 화면 그리기
	public void initDisplay () {
		setContentPane(new mypanal());
		
		/* 버튼과 텍스트필드 구성 */
		jbtn_join.addActionListener(this);
		jbtn_login.addActionListener(this);
		jtf_name.addFocusListener(this);
		jpf_bday.addFocusListener(this);
		this.setLayout(null);
		this.setTitle("AddressBook Program Login");
		this.setSize(350, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setLocation(800, 250);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
//		 id 라인
//		jlb_name.setBounds(45, 200, 130, 40);
//		jlb_name.setFont(jl_font);
		jtf_name.setBounds(70, 250, 200, 40);
		jtf_name.setFont(jl_font2);
		//this.add(jlb_name);
		this.add(jtf_name);

//		 birthday 라인 (비밀번호 역할)
//		jlb_bday.setBounds(45, 240, 130, 40);
//		jlb_bday.setFont(jl_font);
		jpf_bday.setBounds(70, 300, 200, 40);
		jpf_bday.setFont(jl_font2);
		//this.add(jlb_bday);
		this.add(jpf_bday);

		// 로그인 버튼 라인
		jbtn_login.setBounds(70, 350, 200, 40);
		jbtn_login.setFont(jl_font1);
		this.add(jbtn_login);
		jbtn_login.setBorderPainted(true); // JButton의 외곽선 설정
		jbtn_login.setContentAreaFilled(false); // JButton의 내용영역 채우기
		jbtn_login.setFocusPainted(false); // JButton이 선택되었을때 생기는 테두리 설정

		// 회원가입 버튼 라인
		jbtn_join.setBounds(70, 400, 200, 40);
		jbtn_join.setFont(jl_font1);
		this.add(jbtn_join);
		jbtn_join.setBorderPainted(true);
		jbtn_join.setContentAreaFilled(false);
		jbtn_join.setFocusPainted(false);
		
		jlb_logo.setIcon(icon); //////////////////////////////////
		jlb_logo.setBounds(60,60,220,173);
		jlb_logo.setHorizontalAlignment(JLabel.CENTER);
		this.add(jlb_logo);
//		getContentPane().add(jlb_logo);
		
		
	}
	
	/*******************************************************************
	 * 로그인 구현
	 * @param name - 사용자가 입력한 이름 받아오기 (아이디 역할)
	 * @param birthday - 사용자가 입력한 생년월일 받아오기 (비밀번호 역할)
	 * @return id - 사용자 아이디 (시퀀스를 통해 자동 생성됨)
	 *******************************************************************/
	
	public Integer login(String name, String birthday) {
		Integer id = null;
		
		 // 인라인뷰를 통해 rownum 이 1인 데이터만 다시 추출
//		이름(아이디 역할)과 생일(비번역할)이 모두 일치하면 id 출력
//		비번이 틀리면 0 반환
//		이름이 존재하지 않으면 -1 반환
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT                               ");
		sql.append("      id                             ");
		sql.append("FROM(                                ");
		sql.append("   SELECT                            ");
		sql.append("	 CASE WHEN name=? THEN           ");
		sql.append("	   CASE WHEN birthday=? THEN id  ");
        sql.append("      	 ELSE 0                      ");
        sql.append("      	 END                         ");
        sql.append("      ELSE -1                        ");
        sql.append("      END AS id                      ");
        sql.append("     FROM mkaddrtb                   ");
        sql.append("     ORDER BY id desc                "); 
        sql.append("    )                                ");
        sql.append("WHERE ROWNUM = 1                     ");
		try {
			con = dbMgr.getConnection();
	    	pstmt = con.prepareStatement(sql.toString()); // 물음표 채우기
	    	pstmt.setString(1, name);
	    	pstmt.setString(2, birthday);
	    	rs = pstmt.executeQuery();
	    	
	    	// 조회 결과는 0이거나 1을 출력하는 row이므로 if문 사용
	    	if(rs.next()) {
	    		id = rs.getInt("id");
	    	}
	    	System.out.println("id : "+id);
		} catch (SQLException se) {
	    	System.out.println("[[query]]"+ sql.toString());
	    	System.out.println(se.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 사용한 자원 반납은 생성된 역순으로 한다.
			DBConnectionMgr.freeConnection(rs, pstmt, con);
		}
		return id;
	}
	
	public static void main(String[] args) {
		new AddressLogin();
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == jbtn_join) { ////////// 회원가입 버튼 클릭 시 ModifyDialog 호출
			//ModifyDialog mDialog = new ModifyDialog();
			System.out.println("회원가입하기");
			abook = new AddressBook();
			myTableModel = new MyTableModel();
			mDialog = new ModifyDialog(this);
			mDialog.initComponents();
			mDialog.set("회원가입", true, null,abook);
			mDialog.setVisible(true);
			
		} 
		else if (obj == jbtn_login) { //////////// 로그인 버튼 클릭 + 성공 시 AddressBook 호출 
			String name = jtf_name.getText();
			String birthday = jpf_bday.getText();
			Integer id = null;
			
			id = login(name, birthday); // login 메소드에서 반환된 name을 name에 담기
			System.out.println("로그인 요청 결과는? " + name +"님의 아이디 :" +id);
			
			if(id == null) {
				JOptionPane.showMessageDialog(this, "회원가입 여부를 확인하세요");
				return;
			}
			else if(id.equals(0)) {
				JOptionPane.showMessageDialog(this, "비밀번호를 확인하세요.");
				return;
			}
			else if(id.equals(-1)) {
				JOptionPane.showMessageDialog(this, "아이디가 존재하지 않습니다.");
				return;
			}
			else {
				this.dispose(); // 이름이 있다면 로그인창을 닫아라 +  AddressBook 호출!!
				JFrame.setDefaultLookAndFeelDecorated(true);
				abook = new AddressBook();
		        abook.initComponents(); // AddressBook 화면 보이게
		        abook.setVisible(true); // AddressBook 화면 보이게
		        JOptionPane.showMessageDialog(this, name+"님, 환영합니다!", "로그인 완료", JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) { ////////////// 텍스트필드에 포커스를 두면 공백으로 초기화
		if(e.getSource() == jtf_name) {
			jtf_name.setText("");
		}
		else if(e.getSource() == jpf_bday) {
			jpf_bday.setText("");
		}
	}
	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
}