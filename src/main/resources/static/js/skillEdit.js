// CSRFトークンを取得する関数
function getCsrfToken() {
  const token = document.querySelector('meta[name="_csrf"]');
  return token ? token.getAttribute('content') : null;
}

function getCsrfHeader() {
  const header = document.querySelector('meta[name="_csrf_header"]');
  return header ? header.getAttribute('content') : 'X-CSRF-TOKEN';
}

document.addEventListener('DOMContentLoaded', () => {
  const saveButtons = document.querySelectorAll('.btn-save');
  saveButtons.forEach(btn => {
    btn.addEventListener('click', async (e) => {
      e.preventDefault();

      const row = btn.closest('.card-row');
      const id = row.querySelector('input[name$=".id"]').value;
      const name = row.querySelector('input[name$=".learningName"]').value;
      const timeInput = row.querySelector('input[name$=".learningTime"], select[name$=".learningTime"]');

      const time = timeInput.value.trim();
      const errorField = row.querySelector('.error-message');
      if (errorField) errorField.textContent = ''; // エラー初期化

      if (time === '') {
        if (errorField) errorField.textContent = '学習時間は必ず入力してください';
        return;
      }

      const parsed = parseInt(time);
      if (!/^\d+$/.test(time) || isNaN(parsed) || parsed < 1) {
        if (errorField) errorField.textContent = '1以上の数値を入力してください';
        return;
      }


      try {
        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();

        const headers = {
          'Content-Type': 'application/json'
        };
        if (csrfToken) {
          headers[csrfHeader] = csrfToken;
        }

        const response = await fetch('/learning/update', {
          method: 'POST',
          headers: headers,
          body: JSON.stringify({ id: id, learningTime: time })
        });

        if (response.ok) {
          document.getElementById('editItemName').textContent = name;
          const modal = new bootstrap.Modal(document.getElementById('learningEditSuccessModal'));
          modal.show();
        } else {
          alert('保存に失敗しました。');
        }
      } catch (error) {
        console.error(error);
        alert('通信エラーが発生しました。');
      }
    });
  });

  // モーダルから戻るボタン
  document.getElementById('backToEditBtn').addEventListener('click', () => {
    window.location.href = '/learning/edit';
  });
});
