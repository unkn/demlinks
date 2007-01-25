
//set this variable to 1 if you wish the URLs of the highlighted menu to be displayed in the status bar
var display_url=1

var ie5=document.all&&document.getElementById
var ns6=document.getElementById&&!document.all
if (ie5||ns6)
	var menuobj=document.getElementById("contextmenu")
else
	alert("The ContextMenu only works in IE5+ or NS6/Firefox");

function newitem(text, urlaction)
{
        var d1=document.createElement('div');

        if (""!=urlaction) {
                //d1.setAttribute('class','menuitems');
                d1.className='menuitems';
                //d1.setAttribute('url','dothis.php?action='+action+'&onwhat='+escape(id));
                d1.setAttribute('url',urlaction); //'javascript:var something=prompt("Find what ID:",something);');
                d1.style.cursor='pointer';
        } else {
                //d1.setAttribute('class','nonmenuitems');
                d1.className='nonmenuitems';
        }
        d1.appendChild(document.createTextNode(text));
        //d1.innerHTML=text;
        menuobj.appendChild(d1);
}

var renamewith="";
function rename(what)
{
        //renamewith=prompt("newname:",renamewith);
        var parent1=what.parentNode;
        var newinput=document.createElement('input');
        newinput.value=unescape(what.id);
        newinput.className="renamebox";
        var oldone=parent1.replaceChild(newinput, what);
        alert(what.id);
        parent1.replaceChild(oldone,newinput);
}

var passer;
function showmenuie5(e){
//Find out how close the mouse is to the corner of the window
	var onwhat=ie5? event.srcElement : e.target;
	//alert(onwhat.id);
        var onwhatid=unescape(onwhat.id);
        var onwhatclass=onwhat.className;

        try { //rightclicked on an already open contexmenu? do nothing
                if (onwhatclass.indexOf('menuitems') != -1)
                        return false;
        } catch(m) {}

        menuobj.innerHTML="";

        if (onwhatclass=="node" || onwhatclass=="root"||onwhatclass=="leaf") {
                newitem(onwhatid,"");
                newitem('Add New Child...',"javascript:var addchild=prompt(\"Add New Child ID to "+onwhatid+":\",addchild);");
                newitem('Find ID...',"javascript:var findthis=prompt(\"Find what ID:\", findthis);");
                passer=onwhat;
                newitem('Rename ID...',"javascript:rename(passer);");
                //newitem('<a href="javascript:ddtreemenu.flatten(\''+eemenuid.'\', \'expand\')">Expand All</a> | <a href="javascript:ddtreemenu.flatten(\''.$treemenuid.'\', \'contract\')">Contract All</a>'.rnl;

        } else {
                newitem("nothing to do!","",onwhatid);
        }

//position of menu
	var rightedge=ie5? document.body.clientWidth-event.clientX : window.innerWidth-e.clientX
	var bottomedge=ie5? document.body.clientHeight-event.clientY : window.innerHeight-e.clientY

	//if the horizontal distance isn't enough to accomodate the width of the context menu
	if (rightedge<menuobj.offsetWidth)
		//move the horizontal position of the menu to the left by it's width
		menuobj.style.left=ie5? document.body.scrollLeft+event.clientX-menuobj.offsetWidth : window.pageXOffset+e.clientX-menuobj.offsetWidth
	else
		//position the horizontal position of the menu where the mouse was clicked
		menuobj.style.left=ie5? document.body.scrollLeft+event.clientX : window.pageXOffset+e.clientX

//same concept with the vertical position
	if (bottomedge<menuobj.offsetHeight)
		menuobj.style.top=ie5? document.body.scrollTop+event.clientY-menuobj.offsetHeight : window.pageYOffset+e.clientY-menuobj.offsetHeight
	else
		menuobj.style.top=ie5? document.body.scrollTop+event.clientY : window.pageYOffset+e.clientY

//show menu:
	menuobj.style.visibility="visible"
	return false
}

function semihidemenu(e){
	var firingobj=ie5? event.srcElement : e.target
        try {
                if (firingobj.className.indexOf('menuitems') == -1)
                menuobj.style.visibility="hidden"
        } catch(m) {}
        //dumbvar=false;
}

function highlightie5(e){
	var firingobj=ie5? event.srcElement : e.target
	if (firingobj.className=="menuitems"||ns6&&firingobj.parentNode.className=="menuitems"){
		if (ns6&&firingobj.parentNode.className=="menuitems") firingobj=firingobj.parentNode //up one node
		//firingobj.style.backgroundColor="highlight"
		//firingobj.style.color="white"
		if (display_url==1) {//only works in IE && ff, both need to allow status to be changed
			window.status=(ie5? event.srcElement.url:e.target.getAttribute('url'));
                }
	}
}

function lowlightie5(e){
	var firingobj=ie5? event.srcElement : e.target
	if (firingobj.className=="menuitems"||ns6&&firingobj.parentNode.className=="menuitems"){
		if (ns6&&firingobj.parentNode.className=="menuitems") firingobj=firingobj.parentNode //up one node
		//firingobj.style.backgroundColor=""
		//firingobj.style.color="black"
		window.status=''
	}
}

function jumptoie5(e){
	var firingobj=ie5? event.srcElement : e.target
	if (firingobj.className=="menuitems"||ns6&&firingobj.parentNode.className=="menuitems"){
		if (ns6&&firingobj.parentNode.className=="menuitems") firingobj=firingobj.parentNode
                //alert('matah');
		if (firingobj.getAttribute("target"))
		        window.open(firingobj.getAttribute("url"),firingobj.getAttribute("target"))
	        else
		        window.location=firingobj.getAttribute("url")
	}
}

if (ie5||ns6){
	menuobj.style.display=''
	document.oncontextmenu=showmenuie5
	document.onclick=semihidemenu
}

