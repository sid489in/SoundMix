$(document).ready(function() {/* affix the navbar after scroll below header */
	if($('#nav')){
		$('#nav').affix({
			offset : {
				top : $('header').height() - $('#nav').height()
			}
		});	
	}

	/* highlight the top nav as scrolling occurs */
	$('body').scrollspy({
		target : '#nav'
	})

	/* smooth scrolling for scroll to top */
	$('.scroll-top').click(function() {
		$('body,html').animate({
			scrollTop : 0
		}, 1000);
	})


});