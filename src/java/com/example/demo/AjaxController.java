package com.example.demo;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cti.Ajax.service.AddFtoSimple;
import com.cti.Ajax.service.DoneSimpleController;
import com.cti.Ajax.service.FindFsimpleOperation;
import com.test.service.IPMACInquery;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/ajax")
@Controller
@RestController
public class AjaxController {
	@RequestMapping("/a")
	
	public void ajax(String name, HttpServletResponse response,HttpServletRequest request, Model model) throws IOException {
		 String userAgent = request.getHeader("User-Agent");
		String computerName=null;
		 HttpSession session = request.getSession();
		 computerName=(String) session.getAttribute("username")+" ";
		 
	//	 System.out.println(userAgent);
		 // computerName = userAgent.substring(userAgent.indexOf("Chrome")) ;
		 // computerName=computerName.substring(7, 17);
		 // System.out.println(computerName);
		
		DoneSimpleController dsc = new DoneSimpleController();
		String clientIP = request.getRemoteAddr(); 
		try {
			
			 //  if(clientIP.contains("unknow")) {
				//   model.addAttribute("feedbackMessage", "无法获取有效的客户端 IP 地址！");
				//   System.out.println("无法获取有效的客户端 IP 地址！");
				  // return clientIP;
		           
			  // }else {
				 // String  computerIP=clientIP+" ";
				  dsc.doneMethord(name,computerName);
				  // System.out.println(computerIP);
				   
			  // }
			
		} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		//IPMACInquery mac=new IPMACInquery();
		//return clientIP;
		   
		   // String computerName = mac.getMAC(request);
//		 String clientIP = request.getRemoteAddr(); 
//		   if(clientIP.contains("unknow")) {
//			   model.addAttribute("feedbackMessage", "无法获取有效的客户端 IP 地址！");
//			// 转发到模板页面（template.html）
//	            return "testing";  // 返回到 template.html 页面
//		   }else {
//			   String computerName=clientIP+" ";
//			   DoneSimpleController dsc = new DoneSimpleController();
//			  
//			    try {
//			        dsc.doneMethord(name, computerName);
//			        return null;
//			    } catch (ParseException e) {
//			        e.printStackTrace();
//			    }
//		   }
//		return null;
//		
//		   
		 // 通过 ARP 获取对应IP的 MAC 地址
		   
		    // 根据不同的 IP 地址分配不同的 computerName
//		    if (clientIP.equals("192.168.1.101")) {
//		        computerName = "Client1";
//		    } else if (clientIP.equals("192.168.1.102")) {
//		        computerName = "Client2";
//		    } else {
//		        computerName = "UnknownClient";
//		    }

		    // 使用 computerName 执行你之前的逻辑
		   
	}

	@RequestMapping("/b")
	public void ajax2(String simpleNum,String id, String simpleName, String table_name, HttpServletResponse response,
			Model model) {
		AddFtoSimple ats = new AddFtoSimple();
		ats.getNumber(simpleNum,id, simpleName, table_name, model);
	}

	@RequestMapping("/b1")
	public void addSelomenFtoTodayTable(String simpleNum, String id, String table_name, HttpServletResponse response,
			Model model) {
		AddFtoSimple ats = new AddFtoSimple();
		ats.setSelomenToFlabel(simpleNum, id, table_name, model);
	}

	@RequestMapping("/c")
	public void addFinfo(String simpleNum, String simpleName, String firstTie, String lastTie, String Fmethod,
			HttpServletResponse response, Model model) {
		
		SetFDataController sdc = new SetFDataController();
		sdc.setFtieMethord(simpleNum, simpleName, firstTie, lastTie);
	}

	@RequestMapping("/c_addFmet")
	public void addFmethod(String simpleNum, String simpleName, String Fmethod, HttpServletResponse response,
			Model model) {
		SetFDataController sdc = new SetFDataController();
		sdc.setFtieMethord(simpleNum, simpleName, Fmethod);
	}

	@RequestMapping("/d")
	public void insertReacordingData(String simpleNum,String simpleID, String simpleName, String data, String table_name,
			HttpServletResponse response, Model model) {
		ReacordingController rc = new ReacordingController();
		rc.setDataMethord(simpleNum,simpleID, simpleName, data, table_name);
	}

	@RequestMapping("/e")
	public void findFSimple(String simpleNum, String simpleName, String loccon, HttpServletResponse response,
			Model model) {
		FindFsimpleOperation ffs = new FindFsimpleOperation();
		ffs.insertLoc(simpleNum, simpleName, loccon);
	}

	@RequestMapping("/e2")
	public void checkFindedFSimple(String simpleNum, String simpleName, String loccon, HttpServletResponse response,
			Model model) {
		FindFsimpleOperation ffs = new FindFsimpleOperation();
		ffs.checkFinded(simpleNum, simpleName, model);
	}

	@RequestMapping("/f1")
	public void addSelomenToTable(String simpleNum, String simpleName, String table_name, HttpServletResponse response,
			Model model) {
		ReacordingController rc = new ReacordingController();
		rc.addSelomen(simpleNum, simpleName, table_name);
	}

	@RequestMapping("/f2")
	public void addFSelomenToTable(String simpleNum, String simpleName, String table_name, HttpServletResponse response,
			Model model) {
		ReacordingController rc = new ReacordingController();
		rc.addFSelomen(simpleNum, simpleName, table_name);
	}

	@RequestMapping("/g1")
	public void addSelomenDHLdata(String simpleNum, String dhl, String table_name, HttpServletResponse response,
			Model model) {
		SetselomenDataMethord sdm = new SetselomenDataMethord();
		sdm.setseldataofdhl(simpleNum, dhl, table_name);
	}

	@RequestMapping("/g2")
	public void addSelomenXLDdata(String simpleNum, String xld, String table_name, HttpServletResponse response,
			Model model) {
		SetselomenDataMethord sdm = new SetselomenDataMethord();
		sdm.setseldataofxld(simpleNum, xld, table_name);
	}

	@RequestMapping("/g3")
	public void addSelomenTSIandGBUNdata(String simpleNum, String tsi, String gbun, String table_name,
			HttpServletResponse response, Model model) {
		SetselomenDataMethord sdm = new SetselomenDataMethord();
		sdm.setseldataoftsiandGbun(simpleNum, tsi, gbun, table_name);
	}

	@RequestMapping("/g4")
	public void addSelomenOHdata(String simpleNum, String O, String H, String table_name, HttpServletResponse response,
			Model model) {
		SetselomenDataMethord sdm = new SetselomenDataMethord();
		sdm.setseldataofOH(simpleNum, O, H, table_name);
	}

	@RequestMapping("/g5")
	public void addSelomenTUBdata(String simpleNum, String t_lys, String lys, String t_kcn, String kcn, String cas,
			String hbun, String man, String sor, String onpg, String table_name, HttpServletResponse response,
			Model model) {
		SelomenResult tubResult = new SelomenResult(lys, kcn, cas, hbun, man, sor, onpg);
		SetselomenDataMethord sdm = new SetselomenDataMethord();
		sdm.setseldataoftubBox(simpleNum, tubResult, table_name);
	}

	@RequestMapping("/g6")
	public void addSelomenResultdata(String simpleNum, String result, String table_name, HttpServletResponse response,
			Model model) {
		SetselomenDataMethord sdm = new SetselomenDataMethord();
		sdm.setseldataResult(simpleNum, result, table_name);
	}

	@RequestMapping("/query")
	@ResponseBody
	public ArrayList<Simple> querySimple(String simpleNum, HttpServletResponse response, Model model) {
		QuerySimple getSimpleList = new QuerySimple();
		getDBtestName table_nameList = new getDBtestName();
		ArrayList<String> list = table_nameList.getTestName();
		ArrayList<Simple> simpleList = new ArrayList<Simple>();

		for (String table_name : list) {

			simpleList.addAll(getSimpleList.getQuerySimple(table_name, simpleNum));

		}
		if (simpleList.size() == 0) {
			simpleList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到",0));
		}
		// System.out.println(simpleNum);
		return simpleList;
	}

	@RequestMapping("/queryfromname")
	@ResponseBody
	public ArrayList<Simple> querySimplefromname(String simpleNam, HttpServletResponse response, Model model) {
		QuerySimple getSimpleList = new QuerySimple();
		getDBtestName table_nameList = new getDBtestName();
		ArrayList<String> list = table_nameList.getTestName();
		ArrayList<Simple> simpleList = new ArrayList<Simple>();

		for (String table_name : list) {

			simpleList.addAll(getSimpleList.getQuerySimplefromname(table_name, simpleNam));

		}
		if (simpleList.size() == 0) {
			simpleList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到",0));
		}
		// System.out.println(simpleNum);
		return simpleList;
	}

	@RequestMapping("/queryFromYearTable")
	@ResponseBody
	public ArrayList<Simple> querySimpleFromYearTable(String oldsimpleNum, HttpServletResponse response, Model model) {
		QuerySimple getSimpleList = new QuerySimple();
		getDBtestName table_nameList = new getDBtestName();
		ArrayList<String> list = table_nameList.getYearTestName();
		ArrayList<Simple> simpleList = new ArrayList<Simple>();

		for (String table_name : list) {

			simpleList.addAll(getSimpleList.getQuerySimplefromYeartable(table_name, oldsimpleNum));

		}
		if (simpleList.size() == 0) {
			simpleList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到",0));
		}
		// System.out.println(simpleNum);
		return simpleList;
	}

	@RequestMapping("/queryFromYearTableforName")
	@ResponseBody
	public ArrayList<Simple> querySimplefromnameFromYearTable(String oldsimpleNam, HttpServletResponse response,
			Model model) {
		QuerySimple getSimpleList = new QuerySimple();
		getDBtestName table_nameList = new getDBtestName();
		ArrayList<String> list = table_nameList.getYearTestName();
		ArrayList<Simple> simpleList = new ArrayList<Simple>();

		for (String table_name : list) {

			simpleList.addAll(getSimpleList.getQuerySimplefromnamefromYeartable(table_name, oldsimpleNam));

		}
		if (simpleList.size() == 0) {
			simpleList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到",0));
		}
		// System.out.println(simpleNum);
		return simpleList;
	}
	@RequestMapping("/QuerySimpletoComparefromYeartable")
	@ResponseBody
	public ArrayList<Simple> compareQueryFromYearTable( 
		    @RequestParam String simpleNam,
		    @RequestParam String company,
		    @RequestParam String testItem, HttpServletResponse response,
			Model model) {
		QuerySimple getSimpleList = new QuerySimple();
		getDBtestName table_nameList = new getDBtestName();
		ArrayList<String> list = table_nameList.getYearTestName();
		ArrayList<Simple> simpleList = new ArrayList<Simple>();

		for (String table_name : list) {

			simpleList.addAll(getSimpleList.getQuerySimpletoComparefromYeartable(table_name, simpleNam, company, testItem));

		}
		if (simpleList.size() == 0) {
			simpleList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到",0));
		}
		// System.out.println(simpleNum);
		return simpleList;
	}

}
