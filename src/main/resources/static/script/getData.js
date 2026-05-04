async function getData(){
	const requestURL="http://192.168.2.8:8081/json/test.json";
	const request=new Request(requestURL);
	const response=await fetch(request);
	const elements=await response.json();
	console.log(elements);
	
}
getData();