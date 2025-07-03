document.addEventListener('DOMContentLoaded', function () {

  function toHalfWidth(str) { // 全角を半角に変換
    return str.replace(/[！-～]/g, function (ch) {
      return String.fromCharCode(ch.charCodeAt(0) - 0xFEE0);
    }).replace(/　/g, ' ');
  }

  const submitBtn = document.querySelector('#submitButton');
  const form = document.getElementById('skillForm');

  const errorName = document.getElementById("error-learningName");
  const errorTime = document.getElementById("error-learningTime");

  submitBtn.addEventListener('click', function () {

    errorName.textContent = "";

    const learningName = document.getElementById('learningName').value.trim();
    const learningTimeInput = document.getElementById('learningTime').value.trim();
    const learningTimeRaw = toHalfWidth(learningTimeInput);
    
    let learningTime;
    if (learningTimeRaw === "") {
      learningTime = null; // 空欄 → @NotNull で「必ず入力してください」
    } else if (isNaN(Number(learningTimeRaw))) {
      learningTime = -1;   // 無効文字列 → @Min(0) で「0以上で入力してください」
    } else {
      learningTime = Number(learningTimeRaw);
    }
        
    const categoryId = Number(document.getElementById('categoryId').value);
    const learningMonth = document.getElementById('learningMonth').value;

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch('/api/skill/new', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
      },
      body: JSON.stringify({
        learningName,
        learningTime,
        categoryId,
        learningMonth
      })
    })
    .then(async response => {
      if (!response.ok) {
        const errorData = await response.json();
        throw errorData;
      }
      return response.json();
    })
    .then(data => {
      document.getElementById('itemCategory').textContent = data.categoryName;
      document.getElementById('itemName').textContent = data.learningName;
      document.getElementById('itemTime').textContent = data.learningTime + "分";
    
      const modal = new bootstrap.Modal(document.getElementById('successModal'));
      modal.show();
    
      document.getElementById('backToEdit').onclick = function () {
        window.location.href = `/learning/edit?month=${data.learningMonth.slice(0, 7)}`;
      };
    })
    .catch(errorData => {
      errorName.textContent = "";
      errorTime.textContent = "";
    
      if (errorData.learningName) {
        errorName.textContent = errorData.learningName;
      }
      if (errorData.learningTime) {
        errorTime.textContent = errorData.learningTime;
      }
    
      if (!errorData.learningName && !errorData.learningTime) {
        alert("登録に失敗しました。");
        console.error("予期しないエラー内容:", errorData);
      }
    });
      });

  const toggleButton = document.getElementById('toggleDropdown');
  const dropdownList = document.getElementById('dropdownList');
  const input = document.getElementById('learningTime');

  toggleButton.addEventListener('click', function (e) {
    e.stopPropagation();
    dropdownList.style.display = dropdownList.style.display === 'block' ? 'none' : 'block';
  });

  dropdownList.addEventListener('click', function (e) {
    if (e.target && e.target.tagName === 'LI') {
      input.value = e.target.dataset.value;
      dropdownList.style.display = 'none';
    }
  });

  document.addEventListener('click', function (e) {
    if (!dropdownList.contains(e.target) && e.target !== toggleButton && e.target !== input) {
      dropdownList.style.display = 'none';
    }
  });

});
