package edu.iiitb.action;

import java.util.ArrayList;

import com.opensymphony.xwork2.ActionSupport;

import edu.iiitb.database.DBHandlerForUser;
import edu.iiitb.model.Linklists;
import edu.iiitb.model.ProductInfo;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SearchAction extends ActionSupport{

	ArrayList<ProductInfo> productinfo;
	String categoryname, categoryid;
	ArrayList<String> companyList;
	ArrayList<Linklists> linktoitem;
	ArrayList<String> categoryList, categoryListtemp;
	String parentcatname;
	

	public String getParentcatname() {
		return parentcatname;
	}

	public void setParentcatname(String parentcatname) {
		this.parentcatname = parentcatname;
	}

	public String getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}

	public ArrayList<String> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<String> categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList<Linklists> getLinktoitem() {
		return linktoitem;
	}

	public void setLinktoitem(ArrayList<Linklists> linktoitem) {
		this.linktoitem = linktoitem;
	}

	public ArrayList<String> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(ArrayList<String> companyList) {
		this.companyList = companyList;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public ArrayList<ProductInfo> getProductinfo() {
		return productinfo;
	}

	public void setProductinfo(ArrayList<ProductInfo> productinfo) {
		this.productinfo = productinfo;
	}
	
	public String execute()
	{
		DBHandlerForUser dbHandlerForUser = new DBHandlerForUser();
		boolean flag;
		try
		{
			categoryList = new ArrayList<String>();
			categoryListtemp = new ArrayList<String>();
			
			categoryid = dbHandlerForUser.getidonname(categoryname);
			// get id of the product we have
			
			categoryList.add(categoryid);
			// add it to the main list
			
			if(parentcatname == null)
				flag=false;
			else
				flag=true;
			
			if((flag) && (parentcatname.equalsIgnoreCase("Men") || parentcatname.equalsIgnoreCase("Women")))
			{
				int count = categoryList.size();
				for(int i=0; i<count; i++)
				{
					categoryListtemp = dbHandlerForUser.getCategoryListwithcategory(categoryList.get(i), parentcatname);
					if(categoryListtemp.size() > 0)
					{
						for(int j=0; j<categoryListtemp.size(); j++)
						{
							// add it to the main list
							categoryList.add(categoryListtemp.get(j));
						}
					}
				}
				// getting value for the level where we have to decide the path
				// adding it to the main side again
				
				// get the sub-sub category list if present
				for(int i=count; i<categoryList.size(); i++)
				{
					//System.out.println("It is here");
					categoryListtemp = dbHandlerForUser.getCategoryList(categoryList.get(i));
					if(categoryListtemp.size() > 0)
					{
						for(int j=0; j<categoryListtemp.size(); j++)
						{
							// add it to the main list
							categoryList.add(categoryListtemp.get(j));
						}
					}
				}
			}
			else
			{
				
				categoryListtemp = dbHandlerForUser.getCategoryList(categoryid);
				// get the sub category list for the first time
				
				for(int i=0; i<categoryListtemp.size(); i++)
				{
					//System.out.println("value in category list is : " + categoryList.get(i));
					categoryList.add(categoryListtemp.get(i));
				}
				// add it to the main list
				// get the sub-sub category list if present
				for(int i=0; i<categoryList.size()-1; i++)
				{
					categoryListtemp = dbHandlerForUser.getCategoryList(categoryList.get(i+1));
					if(categoryListtemp.size() > 0)
					{
						for(int j=0; j<categoryListtemp.size(); j++)
						{
							// add it to the main list
							categoryList.add(categoryListtemp.get(j));
						}
					}
				}
			}
	
			// Function to get me all sub category id
			productinfo = dbHandlerForUser.getproductlistoncategory(categoryList); 
			// To get the List of all the product and their details
			for(int i=0; i<productinfo.size(); i++)
			{
				if(productinfo.get(i).getOffer() != 0)
				{
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date date = new Date();
					long diff = (productinfo.get(i).getOfferValidity().getTime() - date.getTime());
					int diffDays =(int) Math.ceil(diff / (24.0 * 60.0 * 60.0 * 1000.0));
					productinfo.get(i).setValid(diffDays);
				}
				else
				{
					productinfo.get(i).setValid(-1);
				}
				
				int discount = 100 - (((productinfo.get(i).getPrice()-productinfo.get(i).getOffer())*100)/productinfo.get(i).getPrice());
				productinfo.get(i).setDiscount(discount);
			}
			// calculating offer valid and discount
			linktoitem = dbHandlerForUser.getlinktothecategory(categoryname);
			// To get the links for the side results
			companyList = dbHandlerForUser.getCompanylistoncategory(categoryList);
			// To get the List of all the company for the following product
		}
		catch(Exception e)
		{
			System.out.println("Error Search Action "+e);
			return "error";
		}
		return "success";
	}
}
