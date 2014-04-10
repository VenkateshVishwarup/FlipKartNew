/*package edu.iiitb.action;

import java.sql.SQLException;
import java.util.ArrayList;
import com.opensymphony.xwork2.ActionSupport;
import edu.iiitb.database.DBHandlerforComparison;
import edu.iiitb.model.CompareProductsModel;

public class CompareProducts extends ActionSupport 
{
	
	/**
 * 
 */
/*private static final long serialVersionUID = 1L;
private int productId;
private int count;
private ArrayList<CompareProductsModel> products;

public CompareProducts(int productId, ArrayList<CompareProductsModel> products)
{
	super();
	this.setProductId(productId);
	this.setProducts(products);
}
public CompareProducts() 
{
	
} 
public String getProductDetails() throws SQLException
{
	System.out.println(getProductId());
	setProducts(DBHandlerforComparison.getProducts(getProductId()));
	count=products.size();
	System.out.println("Count is: "+count);
	for(CompareProductsModel c : products)
	{
		System.out.println(c.getProductName());
	}
	return "success";
}

public int getProductId() 
{
	return productId;
}

public void setProductId(int productId) 
{
	this.productId = productId;
}

public ArrayList<CompareProductsModel> getProducts() {
	return products;
}
public void setProducts(ArrayList<CompareProductsModel> products) {
	this.products = products;
}
public int getCount() {
	return count;
}
public void setCount(int count) {
	this.count = count;
}
}
 */

/**
 * 
 */
package edu.iiitb.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.json.JSONPopulator;
import org.apache.struts2.json.JSONUtil;

import com.opensymphony.xwork2.ActionSupport;

import edu.iiitb.model.CompareCartCookie;
import edu.iiitb.model.CompareCartProduct;
import edu.iiitb.model.CompareProductsModel;
import edu.iiitb.database.DBHandlerforComparison;

/**
 * @author PrashantN
 * 
 */
public class CompareProducts extends ActionSupport implements SessionAware,
		ServletResponseAware, ServletRequestAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int productId;
	private ArrayList<CompareProductsModel> products;
	private int count;
	private HttpServletResponse servletResponse;
	private HttpServletRequest servletRequest;

	public CompareProducts() {

	}

	public CompareProducts(int productId,
			ArrayList<CompareProductsModel> products) {
		super();
		this.productId = productId;
		this.products = products;
	}

	/**
	 * @return the productId
	 */
	public int getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(int productId) {
		this.productId = productId;
	}

	/**
	 * @return the products
	 */
	public ArrayList<CompareProductsModel> getProducts() {
		return products;
	}

	/**
	 * @param products
	 *            the products to set
	 */
	public void setProducts(ArrayList<CompareProductsModel> products) {
		this.products = products;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void setServletRequest(HttpServletRequest servletRequest) {
		// TODO Auto-generated method stub
		this.servletRequest = servletRequest;
	}

	@Override
	public void setServletResponse(HttpServletResponse servletResponse) {
		// TODO Auto-generated method stub
		this.servletResponse = servletResponse;
	}

	public String getProductDetails() {
		try {
			System.out.println(productId);
			String content = null;
			boolean cookieFound = false;
			for (Cookie c : servletRequest.getCookies()) {

				if (c.getName().equals("comparecart")) { // what is this for?
					content = c.getValue();
					CompareCartCookie cookie = new CompareCartCookie();
					JSONPopulator pop = new JSONPopulator();
					Map<?, ?> map = (Map<?, ?>) JSONUtil.deserialize(content);
					pop.populateObject(cookie, map);

					if (!cookie.getProductList().contains(
							new CompareCartProduct(productId))) {

						cookie.getProductList().add(
								new CompareCartProduct(productId));

						content = JSONUtil.serialize(cookie);
						c.setValue(content);
						c.setMaxAge(60 * 60 * 24 * 2);
						servletResponse.addCookie(c);
					}
					cookieFound = true;
					break;
				}
			}
			if (cookieFound == false) {
				CompareCartCookie cookie = new CompareCartCookie();
				cookie.getProductList().add(new CompareCartProduct(productId));
				content = JSONUtil.serialize(cookie);
				Cookie c = new Cookie("comparecart", content);
				c.setMaxAge(60 * 60 * 24 * 2);
				servletResponse.addCookie(c);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "success";

	}

	public String getCartProducts() {
		try {

			String content = null;
			boolean cookieFound = false;

			for (Cookie c : servletRequest.getCookies()) {
				if (c.getName().equals("comparecart")) {
					content = c.getValue();
					CompareCartCookie cookie = new CompareCartCookie();
					JSONPopulator pop = new JSONPopulator();
					Map<?, ?> map = (Map<?, ?>) JSONUtil.deserialize(content);
					pop.populateObject(cookie, map);
					products = DBHandlerforComparison
							.getProductsFromCompareCart(cookie.getProductList());
					cookieFound = true;
					break;
				}
			}
			if (cookieFound == false) {
				products = new ArrayList<CompareProductsModel>();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		count = products.size();
		return "success";
	}

	@Override
	public void setSession(Map<String, Object> arg0) {
		// TODO Auto-generated method stub

	}

}
