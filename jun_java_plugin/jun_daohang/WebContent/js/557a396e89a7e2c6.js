qboot.await(function(){return FlashStorage},function(){M("#mysite",function(e,t){function o(){var e=r.get(n);FlashStorage.set(i,JSON.stringify(e))}var n=HAO_CONFIG.mysite.api,r=CacheSVC.ins(hao360.storeNS),i="ns_"+hao360.storeNS+"_"+n,s=r.get(n);t.receive("mysite-panel-turn-off",function(e){o(),QW.W(".tips-restore").removeNode()});if(!s)FlashStorage&&FlashStorage.get(i,function(e){if(e){var i=QW.W,s=JSON.parse(e);if(s&&s.value.length){r.set(n,s),i("#tips-reco-site").hide(),i("#mysite-section").insertAdjacentHTML("beforeend",'<div class="tips-reco-site tips-restore"><span class="tips-reco-comment">\u60a8\u7684\u7f51\u5740\u6ca1\u6709\u52a0\u8f7d\u6210\u529f\uff0c\u8bf7\u60a8</span>&nbsp;&nbsp;<span class="tuijian"><a href="" onclick="return false;" data-type="common" class="btn-reco-site">\u91cd\u65b0\u52a0\u8f7d</a></span></div>'),i("#mysite-section .btn-reco-site").on("click",function(e){i(".tips-restore").removeNode(),R("mysite-list").reRender(),t.tweet("page-height-resize")});var o=new MysiteSvc;o.pushToMemory(),LogHub.behavior("mysite","restore_data")}}});else{var u="cookie4mysite";qboot.cookie.get(u)||(o(),qboot.cookie.set(u,1,{expires:432e5}))}})},null,100,100);