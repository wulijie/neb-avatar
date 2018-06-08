'use strict';

var AvatarContract = function(){
	// value avartarList 要处理分页功能
	LocalContractStorage.defineMapProperty(this, "avartarHall");
	// key=address value=avartarList 要分页功能
	LocalContractStorage.defineMapProperty(this, "userAvartarList");
	//头像id
	LocalContractStorage.defineProperty(this, "avartarNums");
};

AvatarContract.prototype = {
    init: function() {
        this.avartarNums = 0
    },
    //添加头像
    addAvartar: function(avatar) {
       //{"id":0,"name":"xxx.xx","size":"1000","url":"xxx","userId":"address","hash":"hash","ts":"ts"}
       var fromUser = Blockchain.transaction.from,
            time = Blockchain.transaction.timestamp,
            txhash = Blockchain.transaction.hash,
            value = Blockchain.transaction.value;

        if(!avatar){
        	throw new Error("avatar is null")
        }

        var obj = JSON.parse(avatar)

        var avatarList = this.avartarHall.get("key")

        var avatarArray = this.userAvartarList.get(fromUser)


        var addAva = {
        	id: this.avartarNums*1,
        	name: "",
        	size: "",
        	url: "",
        	userId: fromUser,
        	ts: time,
        	hash: ""
        }

        addAva.name = obj.name
        addAva.size = obj.size
        addAva.url = obj.url
        addAva.hash = obj.hash

        if(avatarList){
        	avatarList.unshift(addAva)
        }else{
        	avatarList =[]
        	avatarList.unshift(addAva)
        }

        if(avatarArray){
        	avatarArray.unshift(addAva)
        }else{
        	avatarArray =[]
        	avatarArray.unshift(addAva)
        }      

        var result = {
        	params: obj,
        	result: addAva,
        	id: this.avartarNums*1,
        	list: avatarList
        }

        //类似于自增id
        this.avartarNums += 1


        this.avartarHall.put("key", avatarList)
        this.userAvartarList.put(fromUser,avatarArray)

        return result
    },

    //获取头像列表 分页加载
    getAvartarList: function(page, pagezie){
    	page = parseInt(page) // 0 1 2
        pagezie = parseInt(pagezie) // 20条

        var avatarList = this.avartarHall.get("key")

        var result = {
            total: this.avartarNums*1,
            data: []
        }

        if(!avatarList){
        	return result
        }

        // 页码给-1 直接返回所有的数据
        if(page == -1){
        	result.data = avatarList
        	return result
        }

        var totalNums = this.avartarNums*1

        if(pagezie > totalNums){
        	pagezie = totalNums
        }
        // 4 1 3
        var index = page * pagezie

        var limit = (page + 1) * pagezie

        if(limit > totalNums){
        	limit = totalNums
        }

        for(var i = index;i < limit ; i++){

        	var avatar = avatarList[i]
        	result.data.push(avatar)
        }
        return result
    },

    //根据某个人的地址查询头像列表
    getMyAvartarList: function(address){
    	if(!address){
    		throw new Error("address is null")
    	}

    	var avatarArray = this.userAvartarList.get(address)
    	var result = {
    		userId: address,
            total: 0,
            data: []
        }

        if(!avatarArray){
        	avatarArray = []
        }

        result.total = this.avartarNums*1

        for (var i = 0; i < avatarArray.length; i++) {
        	result.data.push(avatarArray[i])
        }

        return result
    },
};

module.exports = AvatarContract;