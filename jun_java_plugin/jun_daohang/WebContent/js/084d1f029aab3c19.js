(function(){var e=function(e){var t=e.id,n={bottom:e.data.bottom,right:e.data.right,flashUrl:e.data.flash_url,link:e.data.link,width:e.data.width,height:e.data.height,title:e.data.title},r=this._render(n);M({container:r},this._domEvent)};e.prototype._render=function(e){var t='<div class="hotsite-yixing" style="bottom:{$bottom}px;right:{$right}px;height:{$height}px;width:{$width}px"><div class="cont" style="height:{$height}px;width:{$width}px"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="{$width}" height="{$height}"><param name="movie" value="{$flashUrl}" width="{$width}" height="{$height}"><param name="wmode" value="transparent"><param name="allowScriptAccess" value="always"><!--[if !IE]>--><object type="application/x-shockwave-flash" data="{$flashUrl}" width="{$width}" height="{$height}"><param name="wmode" value="transparent"><param name="allowScriptAccess" value="always"><!--<![endif]--><!--[if !IE]>--></object><!--<![endif]--></object></div><a class="link" href="{$link}" title="{$title}" style="height:{$height}px;width:{$width}px"></a></div>';return n||(qboot.load.css(".hotsite-yixing{z-index:99}.hotsite-yixing,.hotsite-yixing .link{position:absolute}.hotsite-yixing .link{background:white;filter:alpha(opacity=0);opacity:0;left:0;top:0;z-index:2}.hotsite-yixing .cont{left:0;position:absolute;top:0;z-index:1}.ie6 .hotsite-yixing .cont{top:-1px}"),n=!0),W(t.tmpl(e)).appendTo(W("#hotsite-view-front")[0])},e.prototype._domEvent=function(e,t){t.receive("mynav-open",function(){t.hide()}),t.receive("mynav-close",function(){t.show()}),t.receive("mysite-panel-turn-on",function(){t.hide()}),t.receive("mysite-panel-turn-off",function(){t.show()})};if(hao360.getFlashVer()<10)return;if(!window.sales||!window.sales.hotsite_yixing)return;var t=0,n=!1,r=window.sales.hotsite_yixing.length;for(;t<r;t++)new e(window.sales.hotsite_yixing[t])})();