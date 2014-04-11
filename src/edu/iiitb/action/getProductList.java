package edu.iiitb.action;

import java.util.ArrayList;

import com.opensymphony.xwork2.ActionSupport;

import edu.iiitb.database.DBHandlerForUser;
import edu.iiitb.model.ProductInfo;

public class getProductList  extends ActionSupport 
{

	ArrayList<ProductInfo> productinfofilter;
	private String category;
	private int count;
	private String brand, price;
	private String[] brandnames;
	private String[] pricelist;
	
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String[] getPricelist() {
		return pricelist;
	}
	public void setPricelist(String[] pricelist) {
		this.pricelist = pricelist;
	}
	public int getCount() {
		return count;
	}
	public String[] getBrandnames() {
		return brandnames;
	}
	public void setBrandnames(String[] brandnames) {
		this.brandnames = brandnames;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public String getCategory() {
		return category;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public ArrayList<ProductInfo> getProductinfofilter() {
		return productinfofilter;
	}
	public void setProductinfofilter(ArrayList<ProductInfo> productinfofilter) {
		this.productinfofilter = productinfofilter;
	}
	
	public String getProductDetailprice()
	{
		if(price != null)
			pricelist = price.split(",");
		DBHandlerForUser dbHandlerForUser = new DBHandlerForUser();
		try
		{
			System.out.println("Category id is : " +category);
			System.out.println("Count id is : " +count);
			System.out.println("Comapany to filter is : " +pricelist[count-1]);
			productinfofilter = dbHandlerForUser.getproductlistoncategoryfilterprice(pricelist,category,count);
		}
		catch(Exception e)
		{
			System.out.println("Error Search Action "+e);
			return "error";
		}
		return "success";
	}
	
	public String getProductDetail()
	{
		if(brand != null)
			brandnames = brand.split(",");
		DBHandlerForUser dbHandlerForUser = new DBHandlerForUser();
		try
		{
			productinfofilter = dbHandlerForUser.getproductlistoncategoryfilter(brandnames,category,count);
		}
		catch(Exception e)
		{
			System.out.println("Error Search Action "+e);
			return "error";
		}
		return "success";
	}
}