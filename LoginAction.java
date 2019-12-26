package com.internousdev.laravel.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.laravel.dao.CartInfoDAO;
import com.internousdev.laravel.dao.UserInfoDAO;
import com.internousdev.laravel.dto.CartInfoDTO;
import com.internousdev.laravel.util.InputChecker;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport implements SessionAware {
	private String password;
	private String userId;
	private String loginFlg;
	private boolean savedUserId;
	private Map<String, Object> session;
	private String errorMessage;
	private CartInfoDAO cartInfoDAO = new CartInfoDAO();
	List<String> stringList = new ArrayList<String>();
	List<String> characterTypeList = new ArrayList<String>();
	private List<String> errorMessageList1;
	private List<String> errorMessageList2;
	private List<CartInfoDTO> cartInfoList = new ArrayList<CartInfoDTO>();
	private String tempUserId;
	private int totalPrice;
	private String isNotUserInfoMessage;

	public String execute() throws SQLException {

		String result = ERROR;
		UserInfoDAO userInfoDAO = new UserInfoDAO();
		// sessionのユーザーID保存の値を削除
		session.remove("saved_user_id");

		// 入力チェックをする
		InputChecker inputChecker = new InputChecker();
		errorMessageList1 = inputChecker.doCheck("ユーザーID", userId, 1, 8, true, false, false, true, false, false);
		errorMessageList2 = inputChecker.doCheck("パスワード", password, 1, 16, true, false, false, true, false, false);

		// 入力エラーがあるとき エラーメッセージを置く、ユーザーID,ログイン保持を返してログイン画面へ
		if (errorMessageList1.size() > 0 || errorMessageList2.size() > 0) {
			session.put("login_flg", 0);
			return result;
		}

		if (session.containsKey("create_user_flag")
				&& Integer.parseInt(session.get("create_user_flag").toString()) == 1) {
			// ユーザー入力完了画面から遷移場合
			userId = session.get("user_id_create").toString();

			// ユーザー情報入力完了画面から遷移した場合にユーザー情報がsessionに入っているため削除
			session.remove("user_id_create");
			session.remove("create_user_flg");
		}

		// 認証処理
		boolean loginCheck = false;
		try {
			loginCheck = userInfoDAO.getUserInfo(userId, password);
		} catch (SQLException e) {
			result = "DBError";
			return result;
		}

		if (loginCheck == true) {

			// ログイン成功のとき 認証情報(userId,saved_user_id,login_flg)をsessionに保存する
			session.put("user_id", userId);
			session.put("login_flg", 1);
			if (savedUserId) {
				session.put("saved_user_id", true);
			}

			tempUserId = session.get("temp_user_id").toString();
			// session.remove("temp_user_id");

			// カート情報がある場合カート情報との紐付けを行う(linkCartInfoメソッドへいく）
			cartInfoList = cartInfoDAO.getUserCartInfo(tempUserId);
			if (cartInfoList.size() > 0) {
				result = linkCartInfo();
				cartInfoList = cartInfoDAO.getUserCartInfo(userId);
				totalPrice = cartInfoDAO.cartTotalPrice(userId);

				return result;
			}
			result = SUCCESS;

			return result;

		} else {
			result = ERROR;
			setIsNotUserInfoMessage("ユーザーIDまたはパスワードが異なります。");

			// ユーザーIDも仮ユーザーIDもなかったらsessionTimeoutする
			if (!session.containsKey("user_id") && !session.containsKey("temp_user_id")) {
				return "sessionTimeout";
			}
		}
		return result;
	}

	// カート情報の紐付けを行う
	private String linkCartInfo() throws SQLException {

		String result = SUCCESS;
		int cartCount = 0;
		// String cartFlg = session.get("cart_flg").toString();

		// 仮ユーザーIDに紐付くカート情報があるか
		String tempUserId = session.get("temp_user_id").toString();
		String userId = session.get("user_id").toString();
		CartInfoDAO cartInfoDAO = new CartInfoDAO();
		List<CartInfoDTO> cartInfoDTO = cartInfoDAO.getUserCartInfo(tempUserId);

		if (cartInfoDTO.size() > 0) {
			for (int i = 0; i < cartInfoDTO.size(); i++) {
				CartInfoDTO dto = new CartInfoDTO();
				dto = cartInfoDTO.get(i);
				int productId = dto.getProductId();
				int productCount = dto.getProductCount();

				boolean existItem = cartInfoDAO.checkCartInfo(userId, productId);

				// もしあれば個数を足した値で更新する
				if (existItem) {
					cartInfoDAO.updateCartInfo(userId, productId, productCount);

					// カート情報削除した件数を変数に代入
					cartCount = cartInfoDAO.delete(tempUserId, productId);

					// もし無ければカート情報のIDをユーザーIDに更新する
				} else if (!existItem) {

					cartCount = cartInfoDAO.addCartInfo(userId, productId, productCount);
				}
			}
			// カート情報紐付けに失敗した場合
			if (cartCount == 0) {
				result = "DBError";
				return result;
			}
			// カート情報紐付けに成功した,かつカートフラグがある場合

			if (cartCount > 0) {
				// cartInfoDAO.getUserCartInfo(userId);
				result = "cart";
				return result;
			}
		}
		return result;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, Object> getSession() {
		return this.session;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public List<String> getErrorMessageList1() {
		return errorMessageList1;
	}

	public void setErrorMessageList1(List<String> errorMessageList1) {
		this.errorMessageList1 = errorMessageList1;
	}

	public List<String> getErrorMessageList2() {
		return errorMessageList2;
	}

	public void setErrorMessageList2(List<String> errorMessageList2) {
		this.errorMessageList2 = errorMessageList2;
	}

	public String getLoginFlg() {
		return loginFlg;
	}

	public void setLoginFlg(String loginFlg) {
		this.loginFlg = loginFlg;
	}

	public boolean getSavedUserId() {
		return savedUserId;
	}

	public void setSavedUserId(boolean savedUserId) {
		this.savedUserId = savedUserId;
	}

	public List<CartInfoDTO> getCartInfoList() {
		return cartInfoList;
	}

	public void setCartInfoList(List<CartInfoDTO> cartInfoList) {
		this.cartInfoList = cartInfoList;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getIsNotUserInfoMessage() {
		return isNotUserInfoMessage;
	}

	public void setIsNotUserInfoMessage(String isNotUserInfoMessage) {
		this.isNotUserInfoMessage = isNotUserInfoMessage;
	}

}
