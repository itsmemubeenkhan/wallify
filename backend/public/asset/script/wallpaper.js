$(document).ready(function () {
  $(".sideBarli").removeClass("activeLi");
  $(".wallpaperSideA").addClass("activeLi");

  $("#wallpaperTable").dataTable({
    processing: true,
    serverSide: true,
    serverMethod: "post",
    aaSorting: [[0, "desc"]],
    language: {
      paginate: {
        next: '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="9 18 15 12 9 6"></polyline></svg>',
        previous:
          '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="15 18 9 12 15 6"></polyline></svg>',
      },
    },
    columnDefs: [
      {
        targets: [0, 1, 2, 3, 4, 5],
        orderable: false,
      },
    ],
    ajax: {
      url: `${domainUrl}wallpaperList`,
      data: function (data) {
        data.wallpaper_type = 0;
      },
      error: (error) => {
        console.log(error);
      },
    },
  });

  $(document).on("submit", "#addWallpaperForm", function (e) {
    e.preventDefault();
    if (user_type == 1) {
      const hasFile = $("#images")[0] && $("#images")[0].files && $("#images")[0].files.length > 0;
      const linksRaw = ($("#content_urls").val() || "").trim();
      const hasLinks = linksRaw.length > 0;

      if (!hasFile && !hasLinks) {
        iziToast.show({
          title: "Error",
          message: "Please upload image(s) or add at least one image URL",
          color: "red",
          position: toastPosition,
          transitionIn: transitionInAction,
          transitionOut: transitionOutAction,
          timeout: 3000,
          animateInside: false,
          iconUrl: `${domainUrl}asset/img/x.svg`,
        });
        return;
      }

      let formData = new FormData($("#addWallpaperForm")[0]);
      $.ajax({
        type: "POST",
        url: `${domainUrl}addWallpaper`,
        data: formData,
        contentType: false,
        processData: false,
        success: function (response) {
          if (response.status == false) {
            console.log(response.message);
            iziToast.show({
              title: "Error",
              message: "Wallpaper not found",
              color: "red",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/copy.svg`,
            });
          } else if (response.status == true) {
            iziToast.show({
              title: "Success",
              message: "Wallpaper Added Successfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
            $("#wallpaperTable").DataTable().ajax.reload(null, false);
            $("#wallpaperModal").modal("hide");
            $("#image_preview").html("");
            $("#content_urls").val("");
          }
        },
      });
    } else {
      iziToast.show({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: false,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  $("#wallpaperTable").on("click", ".edit", function (e) {
    e.preventDefault();

    var id = $(this).attr("rel");
    var tags = $(this).data("tags");
    var category_id = $(this).data("category_id");
    var access_type = $(this).data("access_type");
    var image = $(this).data("image");

    $("#wallpaperID").val(id);

    $("#editCategory").val(category_id);
    $("#editTags").val(tags);
    $("#editType").val(access_type);

    if (image != null) {
      $("#posterImgEdit").attr("src", `${image}`);
    } else {
      $("#posterImgEdit").attr(
        "src",
        "./asset/img/placeholder-image-portrait.png"
      );
    }
    $("#editWallpaperModal").modal("show");
  });

  $(document).on("submit", "#editWallpaperForm", function (e) {
    e.preventDefault();
    var id = $("#wallpaperID").val();
    if (user_type == 1) {
      let editformData = new FormData($("#editWallpaperForm")[0]);
      editformData.append("wallpaper_id", id);
      $.ajax({
        type: "POST",
        url: `${domainUrl}updateWallpaper`,
        data: editformData,
        contentType: false,
        processData: false,
        success: function (response) {
          if (response.status == 404) {
            console.log(response.message);
          } else if (response.status == false) {
            iziToast.show({
              title: "Error",
              message: "Wallpaper not found",
              color: "red",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/x.svg`,
            });
          } else if (response.status == true) {
            iziToast.show({
              title: "Success",
              message: "Wallpaper Update Successfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
            $("#wallpaperTable").DataTable().ajax.reload(null, false);
            // $("#liveWallpaperTable").DataTable().ajax.reload(null, false);
            $("#editWallpaperModal").modal("hide");
          }
        },
      });
    } else {
      iziToast.show({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: false,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  $("#wallpaperTable").on("click", ".delete", function (e) {
    e.preventDefault();
    if (user_type == 1) {
      var id = $(this).attr("rel");
      console.log(id);
      swal({
        title: "Are you sure?",
        icon: "error",
        buttons: true,
        dangerMode: true,
        buttons: ["Cancel", "Yes"],
      }).then((deleteValue) => {
        if (deleteValue) {
          if (deleteValue == true) {
            $.ajax({
              type: "POST",
              url: `${domainUrl}deleteWallpaper`,
              dataType: "json",
              data: {
                wallpaper_id: id,
              },
              success: function (response) {
                if (response.status == false) {
                  console.log(response.message);
                } else if (response.status == true) {
                  iziToast.show({
                    title: "Success",
                    message: "Wallpaper Delete Successfully",
                    color: "green",
                    position: toastPosition,
                    transitionIn: transitionInAction,
                    transitionOut: transitionOutAction,
                    timeout: 3000,
                    animateInside: false,
                    iconUrl: `${domainUrl}asset/img/check-circle.svg`,
                  });
                  $("#wallpaperTable").DataTable().ajax.reload(null, false);
                }
              },
            });
          }
        }
      });
    } else {
      iziToast.show({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: false,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  $("#liveWallpaperTable").dataTable({
    processing: true,
    serverSide: true,
    serverMethod: "post",
    aaSorting: [[0, "desc"]],
    language: {
      paginate: {
        next: '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="9 18 15 12 9 6"></polyline></svg>',
        previous:
          '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="15 18 9 12 15 6"></polyline></svg>',
      },
    },
    columnDefs: [
      {
        targets: [0, 1, 2, 3, 4, 5],
        orderable: false,
      },
    ],
    ajax: {
      url: `${domainUrl}liveWallpaperList`,
      data: function (data) {
        data.wallpaper_type = 1;
      },
      error: (error) => {
        console.log(error);
      },
    },
  });

  $(document).on("click", ".liveWallpaperUrl", function (e) {
    e.preventDefault();

    var live_wallpaper = $(this).data("live_wallpaper");

    $("#showWallpaperUrl").attr("src", `${live_wallpaper}`);

    $("#LiveWallpaperPreviewModal").modal("show");
  });
  $("#LiveWallpaperPreviewModal").on("hide.bs.modal", function () {
    $("#showWallpaperUrl").attr("src", ``);
  });

  $(document).on("submit", "#addLiveWallpaperForm", function (e) {
    e.preventDefault();
    if (user_type == 1) {
      let formData = new FormData($("#addLiveWallpaperForm")[0]);
      $.ajax({
        type: "POST",
        url: `${domainUrl}addLiveWallpaper`,
        data: formData,
        contentType: false,
        processData: false,
        success: function (response) {
          if (response.status == false) {
            console.log(response.message);
            iziToast.show({
              title: "Error",
              message: "Wallpaper not found",
              color: "red",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/copy.svg`,
            });
          } else if (response.status == true) {
            iziToast.show({
              title: "Success",
              message: "Live wallpaper Add Successfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
            $("#liveWallpaperTable").DataTable().ajax.reload(null, false);
            $("#liveWallpaperModal").modal("hide");
            $("#liveWallpaperModal").load(
              location.href + " #liveWallpaperModal>*",
              ""
            );
          }
        },
      });
    } else {
      iziToast.show({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: false,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  $("#liveWallpaperTable").on("click", ".edit", function (e) {
    e.preventDefault();

    var id = $(this).attr("rel");
    var tags = $(this).data("tags");
    var category_id = $(this).data("category_id");
    var access_type = $(this).data("access_type");
    var thumbnail = $(this).data("thumbnail");
    var content = $(this).data("content");

    $("#liveWallpaperID").val(id);
    $("#editLiveWallpaperTags").val(tags);
    $("#editLiveWallpaperCategory").val(category_id);
    $("#editLiveWallpaperType").val(access_type);

    if (thumbnail != null) {
      $("#editLoadthumbnailImage").attr("src", `${thumbnail}`);
    } else {
      $("#editLoadthumbnailImage").attr(
        "src",
        "./asset/img/placeholder-image-portrait.png"
      );
    }
    $("#editVideo").attr("src", `${content}`);

    $("#editLiveWallpaperModal").modal("show");
  });

  $(document).on("submit", "#editLiveWallpaperForm", function (e) {
    e.preventDefault();
    var id = $("#liveWallpaperID").val();
    if (user_type == 1) {
      let editformData = new FormData($("#editLiveWallpaperForm")[0]);
      editformData.append("wallpaper_id", id);
      $.ajax({
        type: "POST",
        url: `${domainUrl}updateLiveWallpaper`,
        data: editformData,
        contentType: false,
        processData: false,
        success: function (response) {
          if (response.status == 404) {
            console.log(response.message);
          } else if (response.status == false) {
            iziToast.show({
              title: "Error",
              message: "Live Wallpaper not found",
              color: "red",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/x.svg`,
            });
          } else if (response.status == true) {
            iziToast.show({
              title: "Success",
              message: "Live Wallpaper Update Successfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
            $("#wallpaperTable").DataTable().ajax.reload(null, false);
            $("#liveWallpaperTable").DataTable().ajax.reload(null, false);
            $("#editLiveWallpaperModal").modal("hide");
          }
        },
      });
    } else {
      iziToast.show({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: false,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  
   $("#liveWallpaperTable").on("click", ".delete", function (e) {
     e.preventDefault();
     if (user_type == 1) {
       var id = $(this).attr("rel");
       console.log(id);
       swal({
         title: "Are you sure?",
         icon: "error",
         buttons: true,
         dangerMode: true,
         buttons: ["Cancel", "Yes"],
       }).then((deleteValue) => {
         if (deleteValue) {
           if (deleteValue == true) {
             $.ajax({
               type: "POST",
               url: `${domainUrl}deleteWallpaper`,
               dataType: "json",
               data: {
                 wallpaper_id: id,
               },
               success: function (response) {
                 if (response.status == false) {
                   console.log(response.message);
                 } else if (response.status == true) {
                   iziToast.show({
                     title: "Success",
                     message: "Wallpaper Delete Successfully",
                     color: "green",
                     position: toastPosition,
                     transitionIn: transitionInAction,
                     transitionOut: transitionOutAction,
                     timeout: 3000,
                     animateInside: false,
                     iconUrl: `${domainUrl}asset/img/check-circle.svg`,
                   });
                   $("#liveWallpaperTable").DataTable().ajax.reload(null, false);
                 }
               },
             });
           }
         }
       });
     } else {
       iziToast.show({
         title: "Oops",
         message: "You are tester",
         color: "red",
         position: toastPosition,
         transitionIn: transitionInAction,
         transitionOut: transitionOutAction,
         timeout: 3000,
         animateInside: false,
         iconUrl: `${domainUrl}asset/img/x.svg`,
       });
     }
   });
  
  $(document).on("change", ".Featured", function (event) {
    event.preventDefault();
       if (user_type == 1) {
         $id = $(this).attr("rel");
         if ($(this).prop("checked") == true) {
           $value = 1;
           console.log("Checkbox is Checked.");
           console.log("1 == true");
           iziToast.show({
             title: "Success",
             message: "Wallpaper Added in Featured item",
             color: "green",
             position: toastPosition,
             transitionIn: transitionInAction,
             transitionOut: transitionOutAction,
             timeout: 3000,
             animateInside: false,
             iconUrl: `${domainUrl}asset/img/check-circle.svg`,
           });
         } else {
           $value = 0;
           console.log("Checkbox is unchecked.");
           console.log("0 == false");
           iziToast.show({
             title: "Success",
             message: "Wallpaper Removed From Featured Item",
             color: "green",
             position: toastPosition,
             transitionIn: transitionInAction,
             transitionOut: transitionOutAction,
             timeout: 3000,
             animateInside: false,
             iconUrl: `${domainUrl}asset/img/check-circle.svg`,
           });
         }
         $.post(
           `${domainUrl}updateFeatured`,
           {
             id: $id,
             is_featured: $value,
           },
           function (returnedData) {
             console.log(returnedData);
             $("#wallpaperTable").DataTable().ajax.reload(null, false);
             $("#liveWallpaperTable").DataTable().ajax.reload(null, false);
           }
         ).fail(function (error) {
           console.log(error);
         });
       } else {
         iziToast.show({
           title: "Oops!",
           message: "You are Tester",
           color: "red",
           position: toastPosition,
           transitionIn: transitionInAction,
           transitionOut: transitionOutAction,
           timeout: 3000,
           animateInside: false,
           iconUrl: `${domainUrl}asset/img/x.svg`,
         });
         $("#wallpaperTable").DataTable().ajax.reload(null, false);
         $("#liveWallpaperTable").DataTable().ajax.reload(null, false);
       }     
  });


});
