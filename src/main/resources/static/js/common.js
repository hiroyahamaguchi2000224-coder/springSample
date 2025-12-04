// グローバルUI/UX制御および二重送信防止
// - フォーム送信時やクリック時にボタン/リンクを無効化して二重送信を防止
// - 'non_disabled' クラスを付与することで無効化を除外可能
// - ブラウザショートカットをブロック: F5リロード、Backspace戻る、Ctrl/Alt + 矢印、Ctrl+R
// - コンテキストメニュー（右クリック）を無効化

(function () {
  'use strict';

  function isOptOut(element) {
    return element.classList && element.classList.contains('non_disabled');
  }

  function disableElement(el) {
    if (!el) return;
    if (isOptOut(el)) return;
    if (el.tagName === 'A') {
      el.dataset.href = el.getAttribute('href') || '';
      el.removeAttribute('href');
      el.classList.add('disabled');
      el.style.pointerEvents = 'none';
      el.style.opacity = '0.6';
    } else if (el.tagName === 'BUTTON' || el.tagName === 'INPUT') {
      if (el.type === 'submit' || el.type === 'button') {
        el.disabled = true;
      }
    }
  }

  function disableSubmitButtons(form) {
    var buttons = form.querySelectorAll('button, input[type="submit"], a');
    buttons.forEach(disableElement);
  }

  // フォーム送信時の二重送信防止
  document.addEventListener('submit', function (e) {
    var form = e.target;
    if (!(form instanceof HTMLFormElement)) return;
    
    // すでに無効化済みの場合はスキップ（処理継続を許可）
    if (form.dataset.submitting === 'true') return;
    
    // フォームを送信中としてマーク
    form.dataset.submitting = 'true';
    
    // ボタンを無効化（非同期で実行し、フォーム送信をブロックしない）
    setTimeout(function() {
      disableSubmitButtons(form);
    }, 0);
  }, true);

  // リンクやボタンクリック時の無効化（サーバーアクション開始用）
  document.addEventListener('click', function (e) {
    var el = e.target;
    if (!(el instanceof Element)) return;

    // アクション可能な要素を検索（button, submit, link）
    var actionable = el.closest('button, input[type="submit"], a');
    if (!actionable) return;

    // 無効化除外の場合はスキップ
    if (isOptOut(actionable)) return;

    // submitボタンの場合はフォーム送信イベントで処理するため、ここではスキップ
    if (actionable.tagName === 'BUTTON' && actionable.type === 'submit') {
      return;
    }
    if (actionable.tagName === 'INPUT' && actionable.type === 'submit') {
      return;
    }

    // リンクやボタン（type=button）のみ即座に無効化
    disableElement(actionable);
  }, true);

  // コンテキストメニューをブロック
  document.addEventListener('contextmenu', function (e) {
    e.preventDefault();
  });

  // キーボード操作によるナビゲーションとリロードをブロック
  document.addEventListener('keydown', function (e) {
    var key = e.key;
    var ctrl = e.ctrlKey;
    var alt = e.altKey;

    // F5リロード
    if (key === 'F5') {
      e.preventDefault();
      return;
    }
    // Ctrl+Rリロード
    if (ctrl && (key === 'r' || key === 'R')) {
      e.preventDefault();
      return;
    }
    // Backspace戻る（input/textarea/contentEditable以外）
    if (key === 'Backspace') {
      var target = e.target;
      var isEditable = (target instanceof HTMLInputElement) ||
        (target instanceof HTMLTextAreaElement) ||
        (target && target.isContentEditable);
      if (!isEditable) {
        e.preventDefault();
        return;
      }
    }
    // Ctrl/Alt + 左右矢印キーでのナビゲーション
    if ((ctrl || alt) && (key === 'ArrowLeft' || key === 'ArrowRight')) {
      e.preventDefault();
      return;
    }
  });


})();

// 共通機能: チェックボックス全選択/解除
// data-toggle-all属性を持つチェックボックスが、data-toggle-targetで指定されたチェックボックスを一括制御
function toggleAll(source) {
  var targetSelector = source.getAttribute('data-toggle-target');
  if (!targetSelector) return;
  document.querySelectorAll(targetSelector).forEach(function(cb) {
    cb.checked = source.checked;
  });
  // ボタン有効化更新
  updateButtonState();
}

// 共通機能: チェックボックス選択数に応じてボタンを有効/無効化
// data-button-enable属性を持つボタンが、data-checkbox-groupで指定されたチェックボックスの選択状況で制御される
function updateButtonState() {
  document.querySelectorAll('[data-button-enable]').forEach(function(btn) {
    var checkboxSelector = btn.getAttribute('data-checkbox-group');
    if (!checkboxSelector) return;
    var checkedCount = document.querySelectorAll(checkboxSelector + ':checked').length;
    btn.disabled = (checkedCount === 0);
  });
}

// DOMContentLoaded後の初期化
document.addEventListener('DOMContentLoaded', function() {
  // チェックボックス変更時にボタン状態更新
  document.querySelectorAll('[data-checkbox-group]').forEach(function(btn) {
    var checkboxSelector = btn.getAttribute('data-checkbox-group');
    if (!checkboxSelector) return;
    document.querySelectorAll(checkboxSelector).forEach(function(cb) {
      cb.addEventListener('change', updateButtonState);
    });
  });
  // 初期表示時のボタン状態設定
  updateButtonState();
});
