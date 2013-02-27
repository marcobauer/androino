		
$('.page-logging').live( 'pageinit', function ( event ) {
		
	$("#log-text").prop("disabled", true);

	$(this).find( '#btn-usb' ).click( function (event) {
		webBridge.log("Click USB")
		$(this).removeClass("ui-btn-active");
	} );
	
	$(this).find( '#btn-led' ).click( function (event) {
		webBridge.log("Click LED")
		$(this).removeClass("ui-btn-active");
	} );

	$(this).find( '#btn-reset' ).click( function (event) {
		$('.page-logging').find( '#log-text' ).text("");
		$(this).removeClass("ui-btn-active");
	} );
		
} );

function addLog( text ) {
	var ele = $('.page-logging').find( '#log-text' );
	ele.text( text + "\n" + ele.text() );
}
