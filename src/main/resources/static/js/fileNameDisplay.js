document.addEventListener('DOMContentLoaded', function () {
  const fileInput = document.getElementById("avatarFile");
  const display = document.getElementById("fileNameDisplay");

  if (fileInput && display) {
    fileInput.addEventListener("change", function (e) {
      const fileName = e.target.files[0]?.name || "";
      display.textContent = fileName;
    });
  }
});
