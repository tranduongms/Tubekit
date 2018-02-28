window.onload = function () {

	$('#mainTabs a').click(function (e) {
		e.preventDefault()
		$(this).tab('show')
	});

	Vue.component('search-item', {
		props: ['keyword', 'willLikeOrDislike', 'comment'],
		template: '<div>\
			<span class="glyphicon glyphicon-search"></span>\
			<span v-if="willLikeOrDislike" class="glyphicon glyphicon-thumbs-up"></span>\
			<span v-if="comment" class="glyphicon glyphicon-comment"></span>\
			{{ keyword }}\
			<div>{{ comment }}</div>\
		</div>'
	});

	Vue.component('link-item', {
		props: ['url', 'willLikeOrDislike', 'comment'],
		template: '<div>\
			<span class="glyphicon glyphicon-link"></span>\
			<span v-if="willLikeOrDislike" class="glyphicon glyphicon-thumbs-up"></span>\
			<span v-if="comment" class="glyphicon glyphicon-comment"></span>\
			{{ url }}\
			<div>{{ comment }}</div>\
		</div>'
	});

	Vue.component('playlist-item', {
		props: ['url', 'loop', 'shuffle'],
		template: '<div>\
			<span class="glyphicon glyphicon-list"></span>\
			<span v-if="loop" class="glyphicon glyphicon-retweet"></span>\
			<span v-if="shuffle" class="glyphicon glyphicon-random"></span>\
			{{ url }}\
		</div>'
	});

	Vue.component('play-item', {
		props: ['item'],
		template: '<div>\
			<search-item v-if="item.type==\'search\'" :keyword="item.keyword" :willLikeOrDislike="item.willLikeOrDislike" :comment="item.comment"></search-item>\
			<link-item v-if="item.type==\'link\'" :url="item.url" :willLikeOrDislike="item.willLikeOrDislike" :comment="item.comment"></link-item>\
			<playlist-item v-if="item.type==\'playlist\'" :url="item.url" :loop="item.loop" :shuffle="item.shuffle"></playlist-item>\
			</div>'
	});	

	var app = new Vue({
		el: '#app',

		data: {
			showAddVideo: true,
			addKeyword: {},
			addLink: {},
			addList: {},
			vpss: []
		},

		computed: {
			bots: function () {
				let bots = [];
				for (var i = 0; i < this.vpss.length; i++) {
					let vps = this.vpss[i];
					if ( vps.chrome) {
						let chrome = vps.chrome;
						chrome.vpsName= vps.name;
						chrome.vpsIp = vps.ip;
						bots.push(chrome);
					}
					if (vps.coccoc) {
						let coccoc = vps.coccoc;
						coccoc.vpsName= vps.name;
						coccoc.vpsIp = vps.ip;
						bots.push(coccoc);
					}
				}
				return bots;
			}
		},

		created: function () {
			this.getVPSList(function(err, vpss) {
				if (!err) {
					this.vpss = vpss;
					this.updateAllStatus();
				} else {
					console.log(err);
				}
			}.bind(this));
		},

		methods: {

			getVPSList: function (callback) {
				var xhr = new XMLHttpRequest();
				xhr.onreadystatechange = function() {
					if (this.readyState == 4 && this.status == 200) {
						var txt = this.responseText;
						try {
							var vpss = JSON.parse(txt);
							if(typeof(callback) == 'function') callback(null, vpss);
						} catch (e){
							console.log(e);
							if(typeof(callback) == 'function') callback('vpsList.json can\'t parse');
						}
					}
				};
				xhr.open('GET', '/vpsList.json');
				xhr.send();
			},

			updateAllStatus: function () {
				this.bots = [];
				for (var i = 0; i < this.vpss.length; i++) {
					this.updateStatus(i);
				}
			},

			updateStatus(i) {
				
				let vps = this.vpss[i];
				
				let xhr = new XMLHttpRequest();
				var that = this;
				xhr.onreadystatechange = function() {
					if (this.readyState == 4) {
						if (this.status == 200) {
							try {
								var status = JSON.parse(this.responseText);
								vps.started = true;
								vps.chrome = status.chrome;
								vps.coccoc = status.coccoc;
								Vue.set(that.vpss, i, vps);
								let eventSource = new EventSource('http://' + vps.ip + ':8080/update');
								eventSource.onmessage = function(event) {
									try {
										var data = JSON.parse(event.data);
										if (data.browserName && (data.browserName == 'chrome')) {
											vps.chrome = data;
											return Vue.set(that.vpss, i, vps);
										}
										if (data.browserName && (data.browserName == 'coccoc')) {
											vps.coccoc = data;
											return Vue.set(that.vpss, i, vps);
										}
									} catch (e) {
										console.log(e);
									}
								}.bind(that);
							} catch (e){
								vps.error = 'Status can\'t parse';
								return Vue.set(that.vpss, i, vps);
							}
						} else {
							vps.error = 'Not online or bot not running';
							return Vue.set(that.vpss, i, vps);
						}
					}
				};
				xhr.open('GET', 'http://' + vps.ip + ':8080/status');
				xhr.send();	
			},

			showHideAddVideo: function () {
				this.showAddVideo = !this.showAddVideo;
			},

			showHideDetail: function (bot) {
				bot.showDetail = !bot.showDetail;
				this.$forceUpdate();
			},

			clearPlayedVideos: function () {
				// Only vps that not have error
				let vpss = this.vpss.filter(function(vps){
					return !vps.error;
				});
				for (var i = 0; i < vpss.length; i++) {
					this.callAPI(vpss[i].ip, '/clearPlayedVideos');
				}
			},

			clearAllErrors: function () {
				// Only vps that not have error
				let vpss = this.vpss.filter(function(vps){
					return !vps.error;
				});
				for (var i = 0; i < vpss.length; i++) {
					this.callAPI(vpss[i].ip, '/clearErrors');
				}
			},

			doAddKeyword: function () {
				if (!this.addKeyword.keyword) return;
				// Only vps that not have error
				let vpss = this.vpss.filter(function(vps){
					return !vps.error;
				});
				let data = '';
				data += 'keyword=' + encodeURIComponent(this.addKeyword.keyword) + '&';
				data += 'willLikeOrDislike=' + encodeURIComponent(this.addKeyword.willLikeOrDislike ? this.addKeyword.willLikeOrDislike : '');
				let comments;
				if (this.addKeyword.comments) comments = this.addKeyword.comments.split('\n');
				if (comments && comments.length >0) {
					for (let i = 0; i < comments.length; i++) {
						let r = Math.round(Math.random()*(vpss.length-1));
						vpss[r].comment = comments[i];
					}
					for (let i = 0; i < vpss.length; i++) {
						let dataWithComment = data + '&comment=' + encodeURIComponent(vpss[i].comment ? vpss[i].comment : '');
						this.callAPI(vpss[i].ip, '/keyword', dataWithComment);
					}
				} else {
					for (var i = 0; i < vpss.length; i++) {
						this.callAPI(vpss[i].ip, '/keyword', data);
					}
				}
				this.addKeyword = {};
			},

			doAddLink: function () {
				if (!this.addLink.url) return;
				// Only vps that not have error
				let vpss = this.vpss.filter(function(vps){
					return !vps.error;
				});
				let data = '';
				data += 'url=' + encodeURIComponent(this.addLink.url) + '&';
				data += 'willLikeOrDislike=' + encodeURIComponent(this.addLink.willLikeOrDislike ? this.addLink.willLikeOrDislike : '');
				let comments;
				if (this.addLink.comments) comments = this.addLink.comments.split('\n');
				if (comments && comments.length >0) {
					for (let i = 0; i < comments.length; i++) {
						let r = Math.round(Math.random()*(vpss.length-1));
						vpss[r].comment = comments[i];
					}
					for (let i = 0; i < vpss.length; i++) {
						let dataWithComment = data + '&comment=' + encodeURIComponent(vpss[i].comment ? vpss[i].comment : '');
						this.callAPI(vpss[i].ip, '/link', dataWithComment);
					}
				} else {
					for (var i = 0; i < vpss.length; i++) {
						this.callAPI(vpss[i].ip, '/link', data);
					}
				}
				this.addLink = {};
			},

			doAddList: function () {
				if (!this.addList.url) return;
				// Only vps that not have error
				let vpss = this.vpss.filter(function(vps){
					return !vps.error;
				});
				let data = '';
				data += 'url=' + encodeURIComponent(this.addList.url) + '&';
				data += 'loop=' + encodeURIComponent(this.addList.loop ? this.addList.loop : '') + '&';
				data += 'shuffle=' + encodeURIComponent(this.addList.shuffle ? this.addList.shuffle : '') + '&';
				data += 'autoNext=' + encodeURIComponent(this.addList.autoNext ? this.addList.autoNext : '');
				for (var i = 0; i < vpss.length; i++) {
					this.callAPI(vpss[i].ip, '/playlist', data);
				}
				this.addList = {};
			},

			callAPI: function (ip, path, data, callback) {
				var xhr = new XMLHttpRequest();
				xhr.onreadystatechange = function() {
					if (this.readyState == 4 && this.status == 200) {
						if(typeof(callback) == 'function') callback('OK');
					} else {
						if(typeof(callback) == 'function') callback('Error', this.status);
					}
				};
				xhr.open('POST', 'http://' + ip + ':8080' + path);
				xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
				xhr.send(data);
			}
		}

	});
}
