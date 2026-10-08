$(document).ready(function () {
  $(".sideBarli").removeClass("activeLi");
  $(".settingSideA").addClass("activeLi");

  const eye = document.querySelector(".feather-eye");
  const eyeoff = document.querySelector(".feather-eye-off");
  const passwordField = document.querySelector("input[type=password]");

  eye.addEventListener("click", () => {
    eye.style.display = "none";
    eyeoff.style.display = "block";
    passwordField.type = "text";
  });

  eyeoff.addEventListener("click", () => {
    eyeoff.style.display = "none";
    eye.style.display = "block";
    passwordField.type = "password";
  });

  const eye1 = document.querySelector(".eye1");
  const eyeoff1 = document.querySelector(".eye-off1");
  const passwordField1 = document.querySelector(
    "input#newPassword[type=password]"
  );

  eye1.addEventListener("click", () => {
    eye1.style.display = "none";
    eyeoff1.style.display = "block";
    passwordField1.type = "text";
  });

  eyeoff1.addEventListener("click", () => {
    eyeoff1.style.display = "none";
    eye1.style.display = "block";
    passwordField1.type = "password";
  });

  function buildSettingsFormDataWithoutBinary() {
    const form = document.getElementById("settingsForm");
    const data = new FormData();

    const tokenInput = form.querySelector('input[name="_token"]');
    const appNameInput = form.querySelector('input[name="app_name"]');
    const announcementInput = form.querySelector('textarea[name="announcement_text"]');
    const splashLinkInput = form.querySelector('input[name="splash_media_link"]');
    const featuredLinkInput = form.querySelector('input[name="featured_media_link"]');
    const removeSplashInput = form.querySelector(
      'input[name="remove_splash_media"]'
    );
    const removeFeaturedInput = form.querySelector(
      'input[name="remove_featured_media"]'
    );

    if (tokenInput) data.append("_token", tokenInput.value || "");
    if (appNameInput) data.append("app_name", appNameInput.value || "");
    if (announcementInput) data.append("announcement_text", announcementInput.value || "");
    if (splashLinkInput && splashLinkInput.value) {
      data.append("splash_media_link", splashLinkInput.value.trim());
    }
    if (featuredLinkInput && featuredLinkInput.value) {
      data.append("featured_media_link", featuredLinkInput.value.trim());
    }
    if (removeSplashInput && removeSplashInput.checked) {
      data.append("remove_splash_media", "1");
    }
    if (removeFeaturedInput && removeFeaturedInput.checked) {
      data.append("remove_featured_media", "1");
    }

    return data;
  }

  function uploadSettingAsset(selectedFile, onSuccess, onError) {
    const uploadData = new FormData();
    const tokenInput = document.querySelector('#settingsForm input[name="_token"]');
    if (tokenInput) uploadData.append("_token", tokenInput.value || "");
    uploadData.append("file", selectedFile);

    $.ajax({
      url: `${domainUrl}syncSettingAsset`,
      type: "POST",
      data: uploadData,
      dataType: "json",
      contentType: false,
      cache: false,
      processData: false,
      success: function (response) {
        if (response.status !== true || !response.data) {
          if (onError) onError(response.message || "Upload failed");
          return;
        }
        if (onSuccess) onSuccess(response.data);
      },
      error: function (err) {
        console.log(err);
        if (onError) onError("Asset upload blocked by server");
      },
    });
  }

  function fileToDataUrl(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result);
      reader.onerror = () => reject(new Error("Failed to read file"));
      reader.readAsDataURL(file);
    });
  }

  function sendSettingsAjax(formdata) {
    $.ajax({
      url: `${domainUrl}saveSettings`,
      type: "POST",
      data: formdata,
      dataType: "json",
      contentType: false,
      cache: false,
      processData: false,
      success: function (response) {
        if (response.status == false) {
          iziToast.show({
            title: "Error",
            message: response.message || "Setting update failed",
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
            message: "Setting updated Successfully",
            color: "green",
            position: toastPosition,
            transitionIn: transitionInAction,
            transitionOut: transitionOutAction,
            timeout: 3000,
            animateInside: false,
            iconUrl: `${domainUrl}asset/img/check-circle.svg`,
          });
          $("#reloadContent").load(location.href + " #reloadContent>*", "");
        }
      },
      error: function (err) {
        console.log(err);
      },
    });
  }

  $("#settingsForm").on("submit", function (event) {
    event.preventDefault();

    if (user_type == 1) {
      const formdata = buildSettingsFormDataWithoutBinary();
      const splashInput = document.querySelector('input[name="splash_media"]');
      const featuredInput = document.querySelector('input[name="featured_media"]');
      const selectedFile =
        splashInput && splashInput.files && splashInput.files[0]
          ? splashInput.files[0]
          : null;
      const selectedFeaturedFile =
        featuredInput && featuredInput.files && featuredInput.files[0]
          ? featuredInput.files[0]
          : null;
      const isSplashVideo =
        !!selectedFile && (selectedFile.type || "").toLowerCase().startsWith("video/");
      const isFeaturedVideo =
        !!selectedFeaturedFile &&
        (selectedFeaturedFile.type || "").toLowerCase().startsWith("video/");

      const showError = (message) => {
        iziToast.show({
          title: "Error",
          message: message || "Upload failed",
          color: "red",
          position: toastPosition,
          transitionIn: transitionInAction,
          transitionOut: transitionOutAction,
          timeout: 3000,
          animateInside: false,
          iconUrl: `${domainUrl}asset/img/x.svg`,
        });
      };

      const uploadFeaturedThenSave = async () => {
        if (selectedFeaturedFile) {
          if (selectedFeaturedFile.size > 50 * 1024 * 1024) {
            showError("Max file size is 50MB");
            return;
          }
          // Direct fallback path for videos because some LiteSpeed/WAF setups block multipart video uploads with 403.
          if (isFeaturedVideo) {
            try {
              const dataUrl = await fileToDataUrl(selectedFeaturedFile);
              formdata.append("featured_media_base64", dataUrl);
              formdata.append(
                "featured_media_mime",
                selectedFeaturedFile.type || "application/octet-stream"
              );
              sendSettingsAjax(formdata);
            } catch (e) {
              showError("Featured video read failed");
            }
            return;
          }

          uploadSettingAsset(
            selectedFeaturedFile,
            function (data) {
              formdata.append("featured_media_path", data.path || "");
              formdata.append("featured_media_type", data.type || "");
              sendSettingsAjax(formdata);
            },
            async function (message) {
              try {
                const dataUrl = await fileToDataUrl(selectedFeaturedFile);
                formdata.append("featured_media_base64", dataUrl);
                formdata.append(
                  "featured_media_mime",
                  selectedFeaturedFile.type || "application/octet-stream"
                );
                sendSettingsAjax(formdata);
              } catch (e) {
                showError(message);
              }
            }
          );
          return;
        }
        sendSettingsAjax(formdata);
      };

      if (selectedFile) {
        if (selectedFile.size > 50 * 1024 * 1024) {
          showError("Max file size is 50MB");
          return;
        }
        // Direct fallback path for videos because some LiteSpeed/WAF setups block multipart video uploads with 403.
        if (isSplashVideo) {
          try {
            const dataUrl = await fileToDataUrl(selectedFile);
            formdata.append("splash_media_base64", dataUrl);
            formdata.append(
              "splash_media_mime",
              selectedFile.type || "application/octet-stream"
            );
            uploadFeaturedThenSave();
          } catch (e) {
            showError("Splash video read failed");
          }
          return;
        }

        uploadSettingAsset(
          selectedFile,
          function (data) {
            formdata.append("splash_media_path", data.path || "");
            formdata.append("splash_media_type", data.type || "");
            uploadFeaturedThenSave();
          },
          async function (message) {
            try {
              const dataUrl = await fileToDataUrl(selectedFile);
              formdata.append("splash_media_base64", dataUrl);
              formdata.append(
                "splash_media_mime",
                selectedFile.type || "application/octet-stream"
              );
              uploadFeaturedThenSave();
            } catch (e) {
              showError(message);
            }
          }
        );
        return;
      }

      uploadFeaturedThenSave();
    } else {
      iziToast.error({
        title: "Oops",
        message: "You are tester",
        color: "red",
        position: toastPosition,
        transitionIn: transitionInAction,
        transitionOut: transitionOutAction,
        timeout: 3000,
        animateInside: true,
        iconUrl: `${domainUrl}asset/img/x.svg`,
      });
    }
  });

  $(document).on("submit", "#changePasswordForm", function (e) {
    e.preventDefault();
    if (user_type == 1) {
      let formData = new FormData($("#changePasswordForm")[0]);
      $.ajax({
        type: "POST",
        url: `${domainUrl}changePassword`,
        data: formData,
        contentType: false,
        processData: false,
        success: function (response) {
          if (response.status == false) {
            console.log(response.message);
            iziToast.show({
              title: "Error",
              message: "Old Password does not match",
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
              message: "Password change successfully",
              color: "green",
              position: toastPosition,
              transitionIn: transitionInAction,
              transitionOut: transitionOutAction,
              timeout: 3000,
              animateInside: false,
              iconUrl: `${domainUrl}asset/img/check-circle.svg`,
            });
            $("#changePasswordForm")[0].reset();
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
});
