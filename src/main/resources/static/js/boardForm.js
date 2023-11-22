window.onload = () => {
    document.getElementById('submitBth').addEventListener('click', () => {
        const title = document.getElementById('title').value;
        const content = document.getElementById('content').value;

        if(title == '' || content == '') {
            alert("제목 혹은 내용을 입력해주세요.");
            return;
        }

        document.getElementById('boardForm').submit();
    });
}