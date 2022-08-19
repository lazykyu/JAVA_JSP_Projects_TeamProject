package dao;

import static db.JdbcUtil.close;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vo.ProductDTO;

public class ProductDAO {
	// 싱글톤 디자인 패턴 사용하여 인스턴스 생성
	private static ProductDAO instance = new ProductDAO();
	private ProductDAO() {};
	public static ProductDAO getInstance() {
		return instance;
	}
	
	Connection con;
	public void setCon(Connection con) {
		this.con = con;
	}
	
	// 전체 게시물수 조회 기능
	public int getListCount() {
		int listCount = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COUNT(*) FROM product";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				listCount = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("getListCount() - SQL 구문 오류 : " + e.getMessage());
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
		}
		return listCount;
	}
	
	// 타입별 상품현황란 건수 표시
	public int selectListCount(String Condition) {
		int count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT COUNT(*) FROM product WHERE pd_condition=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, Condition);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println("selectListCount() - SQL구문 오류 - " + e.getMessage());
			e.printStackTrace();
		}
		return count;
	}
	
	public ArrayList<ProductDTO> getProductList(String start_date, String end_date, String pd_subject, String pd_condition, String search_input, int pdPageNum, int listLimit) {
		ArrayList<ProductDTO> list = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int startRow = (pdPageNum - 1) * listLimit;
		
			try {
				String sql = "";
				
				
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, start_date);
				pstmt.setString(2, end_date);
				pstmt.setString(3, pd_subject);
				pstmt.setString(4, pd_condition);
				pstmt.setString(5, search_input);
				pstmt.setInt(6, startRow);
				pstmt.setInt(7, listLimit);
				rs = pstmt.executeQuery();
			} catch (SQLException e) {
				System.out.println("getProductList() - SQL구문 오류 - " + e.getMessage());
				e.printStackTrace();
			}
		
		return list;
	}
	
	// 헤더의 검색 기능 수행
		// searchType(통합검색,제목검색,작가검색)에 따라 그에 맞는 검색어 키워드로 검색
		// 정렬(sort)의 기본값은 pd_num(최신순 last)이고 검색결과에서 사용자가 선택한 정렬방식에 따라 정렬
		public ArrayList<ProductDTO> getSearchProductList(String searchType, String search, String sort) {
			ArrayList<ProductDTO> SearchProductList = new ArrayList<ProductDTO>();
			ProductDTO product = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				if(searchType.equals("subject")) {
					if(sort.equals("last")) {
						String sql = "SELECT * FROM product WHERE pd_subject LIKE ? ORDER BY pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, "%" + search + "%");
						rs = pstmt.executeQuery();
					}
					else if(sort.equals("price")) {
						String sql = "SELECT * FROM product WHERE pd_subject LIKE ? ORDER BY pd_price ASC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, "%" + search + "%");
						rs = pstmt.executeQuery();
					}
					else {
						String sql = "SELECT * FROM product WHERE pd_subject LIKE ? ORDER BY pd_count DESC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, "%" + search + "%");
						rs = pstmt.executeQuery();
					}
				}
				else if(searchType.equals("writer")) {
					if(sort.equals("last")) {
						String sql = "SELECT * FROM product WHERE pd_name LIKE ? ORDER BY pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, "%" + search + "%");
						rs = pstmt.executeQuery();
					}
					else if(sort.equals("price")) {
						String sql = "SELECT * FROM product WHERE pd_name LIKE ? ORDER BY pd_price ASC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, "%" + search + "%");
						rs = pstmt.executeQuery();
					}
					else {
						String sql = "SELECT * FROM product WHERE pd_name LIKE ? ORDER BY pd_count DESC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, "%" + search + "%");
						rs = pstmt.executeQuery();
					}
				}
				else {
					if(sort.equals("last")) {
						String sql = "SELECT * FROM product WHERE pd_subject LIKE ? OR pd_name LIKE ? ORDER BY pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, "%" + search + "%");
						pstmt.setString(2, "%" + search + "%");
						rs = pstmt.executeQuery();
					}
					else if(sort.equals("price")) {
						String sql = "SELECT * FROM product WHERE pd_subject LIKE ? OR pd_name LIKE ? ORDER BY pd_price ASC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, "%" + search + "%");
						pstmt.setString(2, "%" + search + "%");
						rs = pstmt.executeQuery();
					}
					else {
						String sql = "SELECT * FROM product WHERE pd_subject LIKE ? OR pd_name LIKE ? ORDER BY pd_count DESC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, "%" + search + "%");
						pstmt.setString(2, "%" + search + "%");
						rs = pstmt.executeQuery();
					}
				}
					
				while(rs.next()) {
					product = new ProductDTO();
					product.setPd_num(rs.getInt("pd_num"));
					product.setPd_name(rs.getString("pd_name"));
					product.setPd_price(rs.getInt("pd_price"));
					product.setPd_quan(rs.getInt("pd_quan"));
					product.setPd_file(rs.getString("pd_file"));
					product.setPd_subject(rs.getString("pd_subject"));
					product.setPd_content(rs.getString("pd_content"));
					product.setPd_type(rs.getString("pd_type"));
					product.setPd_count(rs.getInt("pd_count"));
					SearchProductList.add(product);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("getSearchProductList() - SQL 구문 오류 : " + e.getMessage());
			} finally {
				close(pstmt);
				close(rs);
			}
			
			return SearchProductList;
		}
		
		// 사용자의 상품 조회 & 사용자가 지정한 방식으로 정렬
		// 선택한 카테고리가 전체목록이면 파라미터로 받아온 타입이 'all' 이고 전체 상품 목록 출력
		// 선택한 카테고리가 '국내도서'나 '해외도서' 일경우 각 타입에 맞는 상품 출력 
		// sort 최신순이 기본값  
		// 사용자가 선택한 정렬방식에 따라 최신순(last), 가격순(price), 판매량순(rate) 로 정렬
		// 같은 값을 가진 상품끼리는 pd_num(PRIMARY KEY) 으로 정렬
		public ArrayList<ProductDTO> getUserProductList(String type, String sort) {
			ArrayList<ProductDTO> ProductList = new ArrayList<ProductDTO>();
			ProductDTO product = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
					
			try {
				if(type.equals("all")) {
					if(sort.equals("last")) {
						String sql = "SELECT * FROM product ORDER BY pd_num DESC";
						pstmt = con.prepareStatement(sql);
						rs = pstmt.executeQuery();
					} 
					else if(sort.equals("price")) {
						String sql = "SELECT * FROM product ORDER BY pd_price ASC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						rs = pstmt.executeQuery();
					}
					else {
						String sql = "SELECT * FROM product ORDER BY pd_count DESC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						rs = pstmt.executeQuery();
					}
				}
				else {
					if(sort.equals("last")) {
						String sql = "SELECT * FROM product WHERE pd_type=? ORDER BY pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, type);
						rs = pstmt.executeQuery();
					} 
					else if(sort.equals("price")) {
						String sql = "SELECT * FROM product WHERE pd_type=? ORDER BY pd_price ASC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, type);
						rs = pstmt.executeQuery();
					}
					else {
						String sql = "SELECT * FROM product WHERE pd_type=? ORDER BY pd_count DESC, pd_num DESC";
						pstmt = con.prepareStatement(sql);
						pstmt.setString(1, type);
						rs = pstmt.executeQuery();
					}
				}
					
				while(rs.next()) {
					product = new ProductDTO();
					product.setPd_num(rs.getInt("pd_num"));
					product.setPd_name(rs.getString("pd_name"));
					product.setPd_price(rs.getInt("pd_price"));
					product.setPd_quan(rs.getInt("pd_quan"));
					product.setPd_file(rs.getString("pd_file"));
					product.setPd_subject(rs.getString("pd_subject"));
					product.setPd_content(rs.getString("pd_content"));
					product.setPd_type(rs.getString("pd_type"));
					product.setPd_count(rs.getInt("pd_count"));
					ProductList.add(product);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("getUserProductList() - SQL 구문 오류 : " + e.getMessage());
			}finally {
				close(pstmt);
				close(rs);
			}
			return ProductList;
		}
		
		
		// 상품 상세 조회 기능
		public ProductDTO getProductDetail(int pd_num) {
			ProductDTO product = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				String sql = "SELECT * FROM product WHERE pd_num = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, pd_num);
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					product = new ProductDTO();
					product.setPd_num(rs.getInt("pd_num"));
					product.setPd_type(rs.getString("pd_type"));
					product.setPd_name(rs.getString("pd_name"));
					product.setPd_price(rs.getInt("pd_price"));
					product.setPd_quan(rs.getInt("pd_quan"));
					product.setPd_file(rs.getString("pd_file"));
					product.setPd_subject(rs.getString("pd_subject"));
					product.setPd_content(rs.getString("pd_content"));
					product.setPd_date(rs.getDate("pd_date"));
				}
			} catch (SQLException e) {
				System.out.println("getProduct() - SQL 구문 오류 : " + e.getMessage());
				e.printStackTrace();
			}
			return product;
		}
	
}
















