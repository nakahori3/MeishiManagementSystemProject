function toggleValue(id) {
    const checkbox = document.getElementById('toggleButton' + id);
    const dynamicInput = document.getElementById('dynamicInput' + id);
    const form = document.getElementById('infoUsers' + id);

    // ユーザーに確認ダイアログを表示
    const confirmed = window.confirm('管理者権限を切り替えます。変更しますか？');

    if (confirmed) {
       
        dynamicInput.value = checkbox.checked ? 'ROLE_GENERAL' : 'ROLE_ADMIN';

        // フォームをサブミット
        form.submit();
    } else {
        
        checkbox.checked = !checkbox.checked;
    }
}