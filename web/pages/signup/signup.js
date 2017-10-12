// $(function () {
    // $("#loginForm").on('submit', function (e) {
    //     e.preventDefault(); // prevent form for submiting for using ajax instead
    //     var file = $("#fileInput").prop('files')[0];
    //     if (file.type === 'text/xml') {
    //         sendFile(file);
    //     } else { // file type != 'text/xml'
    //         alert('Illegal file type, please choose an xml file');
    //     }
    // });

    $(function () {
        // this is the id of the form
        $("#loginForm").submit(function (e) {
            e.preventDefault(); // avoid to execute the actual submit of the form.
            var form = $(this);

            $.ajax({
                type: form.attr('method'),
                url: form.attr('action'),
                data: form.serialize(),
                datatype: 'json',
                success: function (data) {
                    if (data.isSignUpSuccess === true) {
                        window.location.replace(data.url);
                    }else {
                        swal("User name allready exist, please pick another one")
                    }
                }
            });
        });
    });


// function onSubmitButtonClicked(){
//     var userName = document.getElementById("userName").value;
//     var user = new player(userName);
//     user.showName();
// }

