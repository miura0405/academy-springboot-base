document.addEventListener('DOMContentLoaded', function () {
  const submitBtn = document.querySelector('#submitButton');
  const form = document.getElementById('skillForm');

  submitBtn.addEventListener('click', function () {
    document.getElementById("error-learningName").textContent = "";
    document.getElementById("error-learningTime").textContent = "";

    const learningName = document.getElementById('learningName').value.trim();
    const learningTime = document.getElementById('learningTime').value;
    const categoryId = document.getElementById('categoryId').value;
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
      .then(response => {
        if (!response.ok) {
          return response.json().then(data => { throw data });
        }
        return response.json();
      })
      .then(data => {
        if (!data.categoryId || !data.learningMonth) {
          alert("カテゴリまたは月の情報が取得できませんでした");
          return;
        }

        document.getElementById('itemCategory').textContent = data.categoryName;
        document.getElementById('itemName').textContent = data.learningName;
        document.getElementById('itemTime').textContent = data.learningTime + "分";

        const modal = new bootstrap.Modal(document.getElementById('successModal'));
        modal.show();

        document.getElementById('backToEdit').onclick = function () {
          window.location.href = `/learning/edit?month=${data.learningMonth.slice(0, 7)}`;
        };
      })
      .catch(error => {
        document.getElementById("error-learningName").textContent = "";
        document.getElementById("error-learningTime").textContent = "";

        let hasError = false;

        if (error.learningName) {
          document.getElementById("error-learningName").textContent = error.learningName;
          hasError = true;
        }

        if (error.learningTime) {
          document.getElementById("error-learningTime").textContent = error.learningTime;
          hasError = true;
        }

        if (!hasError) {
          alert("登録に失敗しました。");
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
