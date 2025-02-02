const user = document.getElementById("add-user");
const user_open = document.getElementById("user-open");
const user_close = document.getElementById("user-close");
const user_list = document.getElementsByClassName("user-list")[0];

user_open.onclick = function () {
    user.style.display = "block";
}

user_close.onclick = function () {
    user.style.display = "none";
}

window.onclick = function (event) {
    if (event.target == user) {
        user.style.display = "none";
    }
}

var all_users;
//load all project participants
function loadAllUsers() {
    
    const allUserRequest = new XMLHttpRequest();
    allUserRequest.open('GET', url + `users`);
    allUserRequest.setRequestHeader("Content-Type", "application/json");

    allUserRequest.send();
    allUserRequest.onload = () => {
        if( allUserRequest.status === 200 ) {
            const result = JSON.parse(allUserRequest.response).result;
            all_users = result.users
            console.log("ALL USERS");
            console.log(all_users);
            
            const enabled_options = [];
            const disabled_options = [];
            
            all_users.forEach((user) => {
                const user_opt = document.createElement("option");
                user_opt.innerText = user.userName;
                user_opt.value = user.userId;
                user_opt.classList.add("user-list-elem");
                user_opt.addEventListener("click", ()=>{
                    user_opt.classList.toggle("user-selected")
                })
                if (project_users.some(item => item.userName === user.userName)){
                    user_opt.disabled = true;
                    disabled_options.push(user_opt);
                } else {
                    enabled_options.push(user_opt);
                }
                user_list.appendChild(user_opt);
            });
            enabled_options.forEach((e) => {user_list.appendChild(e)});
            user_list.appendChild(document.createElement("hr"));
            disabled_options.forEach((e) => {user_list.appendChild(e)});
        } else {
            alert("전체 유저 목록을 받아오지 못했습니다.");
            console.error("Error", allUserRequest.status, allUserRequest.statusText);
        }
    };
}

//add user
const add_user_btn = document.getElementById("add-user-btn");
add_user_btn.addEventListener("click", addUser);

function addUser() {
    add_user_btn.disabled = true;
    const role_list = document.getElementById("role-list");
    const addUserRequest = new XMLHttpRequest();
    addUserRequest.open('POST', url + `projects/add/${user_list.value}`);
    addUserRequest.setRequestHeader("Content-Type", "application/json");
    var body = JSON.stringify({
        adminId: localStorage.getItem("userId"),
        projectId: project_id,
        userRole: role_list.value
    });
    addUserRequest.send(body);
    addUserRequest.onload = () => {
        if( addUserRequest.status === 200 ) {
            const result = JSON.parse(addUserRequest.response).result;
            console.log(result);
            alert("유저를 프로젝트에 추가하였습니다.");
            add_user_btn.disabled = false;
            location.reload(true);
        } else {
            const err_msg = JSON.parse(addUserRequest.response).message
            alert(err_msg);
            console.log(err_msg);
            console.error("Error", addUserRequest.status, addUserRequest.statusText);
            add_user_btn.disabled = false;
        }
    };
}
