// CSRFトークンを取得する関数
function getCsrfToken() {
  const token = document.querySelector('meta[name="_csrf"]');
  return token ? token.getAttribute("content") : null;
}

function getCsrfHeader() {
  const header = document.querySelector('meta[name="_csrf_header"]');
  return header ? header.getAttribute("content") : "X-CSRF-TOKEN";
}

document.addEventListener("DOMContentLoaded", () => {
  // 保存ボタン処理
  const saveButtons = document.querySelectorAll(".btn-save");
  saveButtons.forEach((btn) => {
    btn.addEventListener("click", async (e) => {
      e.preventDefault();

      const row = btn.closest(".card-row");
      const id = row.querySelector('input[name$=".id"]').value;
      const name = row.querySelector('input[name$=".learningName"]').value;
      const timeInput = row.querySelector(
        'input[name$=".learningTime"], select[name$=".learningTime"]'
      );
      const time = timeInput.value.trim();
      const errorField = row.querySelector(".error-message");
      if (errorField) errorField.textContent = ""; // エラー初期化

      if (time === "") {
        if (errorField)
          errorField.textContent = "学習時間は必ず入力してください";
        return;
      }

      const parsed = parseInt(time);
      if (!/^\d+$/.test(time) || isNaN(parsed) || parsed < 1) {
        if (errorField)
          errorField.textContent = "1以上の数値を入力してください";
        return;
      }

      try {
        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();

        const headers = {
          "Content-Type": "application/json",
        };
        if (csrfToken) {
          headers[csrfHeader] = csrfToken;
        }

        const response = await fetch("/learning/update", {
          method: "POST",
          headers: headers,
          body: JSON.stringify({ id: id, learningTime: time }),
        });

        if (response.ok) {
          document.getElementById("editItemName").textContent = name;
          const modal = new bootstrap.Modal(
            document.getElementById("learningEditSuccessModal")
          );
          modal.show();
        } else {
          alert("保存に失敗しました。");
        }
      } catch (error) {
        console.error(error);
        alert("通信エラーが発生しました。");
      }
    });
  });

  // 削除ボタン処理（confirmなし）
  const deleteButtons = document.querySelectorAll(".btn-delete");
  deleteButtons.forEach((btn) => {
    btn.addEventListener("click", async () => {
      const row = btn.closest(".card-row");
      const id = row.querySelector('input[name$=".id"]').value;
      const name = row.querySelector('input[name$=".learningName"]').value;

      try {
        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();

        const headers = {
          "Content-Type": "application/json",
        };
        if (csrfToken) {
          headers[csrfHeader] = csrfToken;
        }

        const response = await fetch("/learning/delete", {
          method: "POST",
          headers: headers,
          body: JSON.stringify({ id: id }),
        });

        if (response.ok) {
          document.getElementById("deletedItemName").textContent = name;
          const modal = new bootstrap.Modal(
            document.getElementById("deleteSuccessModal")
          );
          modal.show();
        } else {
          alert("削除に失敗しました。");
        }
      } catch (error) {
        console.error(error);
        alert("通信エラーが発生しました。");
      }
    });
  });

  // モーダル「戻る」ボタンで編集画面に戻る（保存成功）
  // モーダル「戻る」ボタンで編集画面に戻る（保存成功）
  const backToEditBtn = document.getElementById("backToEditBtn");
  if (backToEditBtn) {
    backToEditBtn.addEventListener("click", () => {
      const targetMonthInput = document.querySelector(
        'input[name="targetMonth"]'
      );
      const month = targetMonthInput ? targetMonthInput.value : null;
      if (month) {
        window.location.href = `/learning/edit?month=${month}`;
      } else {
        window.location.href = "/learning/edit"; // fallback
      }
    });
  }

  // モーダル「戻る」ボタンで編集画面に戻る（削除成功）
  const backToEditFromDelete = document.getElementById("backToEditFromDelete");
  if (backToEditFromDelete) {
    backToEditFromDelete.addEventListener("click", () => {
      const targetMonthInput = document.querySelector(
        'input[name="targetMonth"]'
      );
      const month = targetMonthInput ? targetMonthInput.value : null;
      if (month) {
        window.location.href = `/learning/edit?month=${month}`;
      } else {
        window.location.href = "/learning/edit";
      }
    });
  }
});
