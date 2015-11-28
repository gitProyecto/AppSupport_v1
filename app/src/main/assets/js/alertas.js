	function showSuccessToast() {
        $().toastmessage('showSuccessToast', "Bienvenido");
    }
       
    function showErrorToast() {
        $().toastmessage('showErrorToast', "Datos incorrectos");
    }
       
    function showStickySuccessToast() {
        $().toastmessage('showToast', {
            text     : 'Success Dialog which is sticky',
            sticky   : true,
            position : 'top-right',
            type     : 'success',
            closeText: '',
            close    : function () {
                console.log("toast is closed ...");
            }
        });

    }
    function showNoticeToast() {
        $().toastmessage('showNoticeToast', "Notice  Dialog which is fading away ...");
    }
    function showStickyNoticeToast() {
        $().toastmessage('showToast', {
             text     : 'Notice Dialog which is sticky',
             sticky   : true,
             position : 'top-right',
             type     : 'notice',
             closeText: '',
             close    : function () {console.log("toast is closed ...");}
        });
    }
    function showWarningToast() {
        $().toastmessage('showWarningToast', "Warning Dialog which is fading away ...");
    }
    function showStickyWarningToast() {
        $().toastmessage('showToast', {
            text     : 'Warning Dialog which is sticky',
            sticky   : true,
            position : 'top-right',
            type     : 'warning',
            closeText: '',
            close    : function () {
                console.log("toast is closed ...");
            }
        });
    }
    
    function showStickyErrorToast() {
        $().toastmessage('showToast', {
            text     : 'Error Dialog which is sticky',
            sticky   : true,
            position : 'top-right',
            type     : 'error',
            closeText: '',
            close    : function () {
                console.log("toast is closed ...");
            }
        });
    }