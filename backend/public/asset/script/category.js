$(document).ready(function () {
  $(".sideBarli").removeClass("activeLi");
  $(".categorySideA").addClass("activeLi");

  // $("#addCategoryType").click(function () {
  //   $("#categoryType").val("0");
  //   $("#categoryModal #exampleModalLabel").text("Add Category");
  // });
  // $("#addLiveCategoryType").click(function () {
  //   $("#categoryType").val("1");
  //   $("#categoryModal #exampleModalLabel").text("Add live Category");
  // });

  $("#categoryTable").dataTable({
    processing: true,
    serverSide: true,
    serverMethod: "post",
    aaSorting: [[0, "desc"]],
    language: {
      paginate: {
        next: '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="9 18 15 12 9 6"></polyline></svg>',
        previous: '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="15 18 9 12 15 6"></polyline></svg>',
      },
    },
    columnDefs: [
      {
        targets: [0, 1, 2, 3],
        orderable: false,
      },
    ],
    ajax: {
      url: `${domainUrl}categoryList`,
      data: function (data) {
        data.type = 0;
      },
      error: (error) => {
        console.log(error);
      },
    },
  });

  $(document).on("submit", "#addCategoryForm", function (e) {
    e.preventDefault();
    if (user_type == 1) {
      let formData = new FormData($("#addCategoryForm")[0]);
      $.ajax({
        type: "POST",
        url: `${domainUrl}addCategory`,
        data: formData,
        contentType: false,
        processData: false,
        success: function (response) {
          if (response.status == false) {
            console.log(response.message);
            iziToast.show({
              title: "Error",
              message: "Category not found",
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
              message: "Category Added Successfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
            $("#categoryTable").DataTable().ajax.reload(null, false);
            $("#liveCategoryTable").DataTable().ajax.reload(null, false);
            $("#categoryModal").modal("hide");
            $("#categoryModal").load(location.href + " #categoryModal>*", "");
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

  $("#categoryTable").on("click", ".edit", function (e) {
    e.preventDefault();

    var id = $(this).attr("rel");
    var title = $(this).data("title");
    var description = $(this).data("description");
    var image = $(this).data("image");

    $("#categoryID").val(id);
    $("#editCategory").val(title);
    $("#editDescription").val(description);

    if (image != null) {
      $("#posterImgEdit").attr("src", `${image}`);
    } else {
      $("#posterImgEdit").attr("src", "./asset/img/placeholder-image.png");
    }

    $("#editCategoryModal").modal("show");
  });

  $(document).on("submit", "#editCategoryForm", function (e) {
    e.preventDefault();
    var id = $("#categoryID").val();
    if (user_type == 1) {
      let editformData = new FormData($("#editCategoryForm")[0]);
      editformData.append("category_id", id);
      $.ajax({
        type: "POST",
        url: `${domainUrl}updateCategory`,
        data: editformData,
        contentType: false,
        processData: false,
        success: function (response) {
          if (response.status == 404) {
            console.log(response.message);
          } else if (response.status == false) {
            iziToast.show({
              title: "Error",
              message: "Category not found",
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
              message: "Category Update Successfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
            $("#categoryTable").DataTable().ajax.reload(null, false);
            $("#liveCategoryTable").DataTable().ajax.reload(null, false);
            $("#editCategoryModal").modal("hide");
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

  $("#categoryTable").on("click", ".delete", function (e) {
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
            console.log(true);
            $.ajax({
              type: "POST",
              url: `${domainUrl}deleteCategory`,
              dataType: "json",
              data: {
                category_id: id,
              },
              success: function (response) {
                if (response.status == false) {
                  console.log(response.message);
                } else if (response.status == true) {
                  iziToast.show({
                    title: "Success",
                    message: "Category Delete Successfully",
                    color: "green",
                    position: toastPosition,
                    transitionIn: transitionInAction,
                    transitionOut: transitionOutAction,
                    timeout: 3000,
                    animateInside: false,
                    iconUrl: `${domainUrl}asset/img/check-circle.svg`,
                  });
                  $("#categoryTable").DataTable().ajax.reload(null, false);
                  $("#liveCategoryTable").DataTable().ajax.reload(null, false);
                  console.log(response.message);
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

  $("#liveCategoryTable").dataTable({
    processing: true,
    serverSide: true,
    serverMethod: "post",
    aaSorting: [[0, "desc"]],
    language: {
      paginate: {
        next: '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="9 18 15 12 9 6"></polyline></svg>',
        previous: '<svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round" class="css-i6dzq1"><polyline points="15 18 9 12 15 6"></polyline></svg>',
      },
    },
    columnDefs: [
      {
        targets: [0, 1, 2, 3],
        orderable: false,
      },
    ],
    ajax: {
      url: `${domainUrl}categoryList`,
      data: function (data) {
        data.type = 1;
      },
      error: (error) => {
        console.log(error);
      },
    },
  });

  $("#liveCategoryTable").on("click", ".edit", function (e) {
    e.preventDefault();

    var id = $(this).attr("rel");
    var title = $(this).data("title");
    var description = $(this).data("description");
    var image = $(this).data("image");

    $("#categoryID").val(id);
    $("#editCategory").val(title);
    $("#editDescription").val(description);

    if (image != null) {
      $("#posterImgEdit").attr("src", `${image}`);
    } else {
      $("#posterImgEdit").attr("src", "./asset/img/placeholder-image.png");
    }

    $("#editCategoryModal").modal("show");
  });

  $("#liveCategoryTable").on("click", ".delete", function (e) {
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
              url: `${domainUrl}deleteCategory`,
              dataType: "json",
              data: {
                category_id: id,
              },
              success: function (response) {
                if (response.status == false) {
                  console.log(response.message);
                } else if (response.status == true) {
                  iziToast.show({
                    title: "Success",
                    message: "Category Delete Successfully",
                    color: "green",
                    position: toastPosition,
                    transitionIn: transitionInAction,
                    transitionOut: transitionOutAction,
                    timeout: 3000,
                    animateInside: false,
                    iconUrl: `${domainUrl}asset/img/check-circle.svg`,
                  });
                  $("#categoryTable").DataTable().ajax.reload(null, false);
                  $("#liveCategoryTable").DataTable().ajax.reload(null, false);
                  console.log(response.message);
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

});
