import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link, NavLink } from 'react-router-dom';
import ContendCard from '../contents/ContentCard';

import $ from 'jquery';
import GET_INFO_TIMEOUT from '../config/GetInfoTimeoutConfig';
import BACKEND_ADDRESS_URL from "../config/BackendAddressURLConfig";
import PAGE_ITEM_LIMIT from '../config/PageItemLimit';

class ProgrammingManage extends Component {
    state = {
        programming_list: [
        ],

        page_number: 0,  // 从0开始

        is_loading: false
    }

    componentDidMount = () => {
        if (this.props.is_login) {
            this.handleGetAll();
        } else {
            setTimeout(this.handleGetAll, GET_INFO_TIMEOUT);
        }
    }

    handleGetAll = () => {
        const token = this.props.token;
        // console.log(token);
        this.setState({ is_loading: true });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_manage/programming_manage/all/",
            type: "GET",
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    programming_list: resp,
                    is_loading: false
                });
                if (resp.error_message) {
                    console.log(resp);
                }
            }
        });
    }

    handleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <tr>
                    <td><span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span></td>
                    <td><span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span></td>
                    <td><span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span></td>
                    <td><span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span></td>
                    <td><span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span></td>
                    <td><span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span></td>
                    <td><span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span></td>
                </tr>
            );
        }
    }

    renderTable = () => {
        const programming_list_display = [];
        for (let i = this.state.page_number * PAGE_ITEM_LIMIT; i < this.state.programming_list.length && i < (this.state.page_number + 1) * PAGE_ITEM_LIMIT; i++) {
            const programming = this.state.programming_list[i];
            programming_list_display.push({
                ...programming
            });
        }

        return (
            <table className="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Title</th>
                        <th scope="col">Tag</th>
                        <th scope="col">Score</th>
                        <th scope="col">Difficulty</th>
                        <th scope="col">Use Count</th>
                        <th scope="col">Author</th>
                    </tr>
                </thead>
                <tbody>
                    {this.handleLoadingRender()}
                    {programming_list_display.map((programming) => {
                        if (!programming.programming_id) {
                            return;
                        }
                        return (
                            <tr key={'p' + programming.programming_id}>
                                <th scope='row'>{programming.programming_id}</th>
                                <td style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "20vw", minWidth: "20vw", textWrap: "nowrap" }}>
                                    <Link className='link-primary' to={`/problem_manage/programming_manage/${programming.programming_id}/`}>
                                        {programming.p_title}
                                    </Link>
                                </td>
                                <td>{programming.p_tag}</td>
                                <td>{programming.p_total_score}</td>
                                <td>{programming.p_difficulty}</td>
                                <td>{programming.p_use_count}</td>
                                <td>{programming.p_author_name}</td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        );
    }

    renderPreviousButton = () => {
        const page_number = this.state.page_number;
        if (this.state.page_number <= 0) {
            return (
                <button type="button" className="btn btn-outline-secondary" disabled>&laquo;</button>
                // <li className="page-item">
                //     <a className="page-link disabled" href="#" aria-label="Previous">
                //         <span aria-hidden="true">&laquo;</span>
                //     </a>
                // </li>
            );
        } else {
            return (
                <button type="button" className="btn btn-outline-primary" onClick={() => { this.setState({ page_number: page_number - 1 }) }}>&laquo;</button>
                // <li className="page-item">
                //     <a className="page-link" href="#" aria-label="Previous" onClick={() => { this.setState({ page_number: page_number - 1 }) }}>
                //         <span aria-hidden="true">&laquo;</span>
                //     </a>
                // </li>
            );
        }
    }

    renderNextButton = () => {
        const page_number = this.state.page_number;
        const total_page_count = Math.ceil(this.state.programming_list.length / PAGE_ITEM_LIMIT);
        // console.log(page_number, total_page_count);
        if (page_number >= total_page_count - 1) {
            return (
                <button type="button" className="btn btn-outline-secondary" disabled>&raquo;</button>
                // <li className="page-item">
                //     <a className="page-link disabled" href="#" aria-label="Next">
                //         <span aria-hidden="true">&raquo;</span>
                //     </a>
                // </li>
            );
        } else {
            return (
                <button type="button" className="btn btn-outline-primary" onClick={() => { this.setState({ page_number: page_number + 1 }) }}>&raquo;</button>
                // <li className="page-item">
                //     <a className="page-link" href="#" aria-label="Next" onClick={() => { this.setState({ page_number: page_number + 1 }) }}>
                //         <span aria-hidden="true">&raquo;</span>
                //     </a>
                // </li>
            );
        }
    }

    renderPageButtons = () => {
        const total_page_count = this.state.programming_list.length === 0 ? 1 : Math.ceil(this.state.programming_list.length / PAGE_ITEM_LIMIT);
        const page_number = this.state.page_number;
        const page_array = Array.from(Array(total_page_count), (_, x) => x);
        // console.log(page_array);
        return (
            <React.Fragment>
                {page_array.map((page) => {
                    if (page === page_number) {
                        return (
                            <button type="button" key={'page' + page} className="btn btn-primary" onClick={() => { this.setState({ page_number: page }) }}>
                                {page + 1}
                            </button>
                            // <li className="page-item active" key={'page' + page}>
                            //     <a className="page-link" href="#">
                            //         {page + 1}
                            //     </a>
                            // </li>
                        );
                    } else {
                        return (
                            <button type="button" key={'page' + page} className="btn btn-outline-primary" onClick={() => { this.setState({ page_number: page }) }}>
                                {page + 1}
                            </button>
                            // <li className="page-item" key={'page' + page}>
                            //     <a className="page-link" href="#" onClick={() => { this.setState({ page_number: page }) }}>
                            //         {page + 1}
                            //     </a>
                            // </li>
                        );
                    }
                })}
            </React.Fragment>
        );
    }

    handlePermissionRender = () => {
        if (this.props.permission > 0) {
            return (
                // real display of this page
                <div className="container">
                    <ContendCard>
                        {/* nav tabs */}
                        <ul className="nav nav-tabs justify-content-center">
                            <li className="nav-item">
                                <NavLink className="nav-link" to="/problem_manage/objective_problem_manage/">Objective Problems</NavLink>
                            </li>
                            <li className="nav-item">
                                <NavLink className="nav-link" to="/problem_manage/programming_manage/">Programming Problems</NavLink>
                            </li>
                        </ul>

                        {/* new p/ button */}
                        <div className="row mt-3">
                            <div className="col-auto">
                                <Link className='btn btn-outline-primary' to={"/problem_manage/programming_manage/create/"}>
                                    Create
                                </Link>
                            </div>
                            <div className="col">
                                {/* button pagination */}
                                <div className='pagination justify-content-end flex-wrap mb-0'>
                                    <div className="btn-toolbar" role="toolbar" aria-label="Toolbar with button groups">
                                        <div className="btn-group me-2" role="group">
                                            {this.renderPreviousButton()}
                                            {this.renderPageButtons()}
                                            {this.renderNextButton()}
                                        </div>
                                    </div>
                                </div>
                                {/* render pagination */}
                                {/* <nav aria-label="Page navigation example">
                                    <ul className="pagination justify-content-end flex-wrap mb-0">
                                        {this.renderPreviousButton()}
                                        {this.renderPageButtons()}
                                        {this.renderNextButton()}
                                    </ul>
                                </nav> */}
                            </div>
                        </div>
                        <hr />

                        {/* p/ table */}
                        {this.renderTable()}
                    </ContendCard>
                </div>
            );
        } else {
            return (
                <div className="container">
                    <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                        <div className="col col-md-7">
                            <ContendCard>
                                <div className="row justify-content-md-center">
                                    <h1 className="text-center">Programming Problems</h1>
                                    <hr />
                                    <h4 className="text-center" style={{ textDecoration: "none" }}>You do not have permission to access this page</h4>
                                </div>
                            </ContendCard>
                        </div>
                    </div>
                </div>

            );
        }
    }

    handleAccountRender = () => {
        if (this.props.is_login) {
            return (
                <React.Fragment>
                    {this.handlePermissionRender()}
                </React.Fragment>
            );
        } else {
            return (
                <div className="container">
                    <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                        <div className="col col-md-7">
                            <ContendCard>
                                <div className="row justify-content-md-center">
                                    <h1 className="text-center">Programming Problems</h1>
                                    <hr />
                                    <h4 className="text-center">Please <Link className='btn btn-link px-0' to="/login/" style={{ textDecoration: "none" }}><h4 className='mb-1'>sign in</h4></Link> to access</h4>
                                </div>
                            </ContendCard>
                        </div>
                    </div>
                </div>
            );
        }
    }

    render() {
        return (
            <React.Fragment>
                {this.handleAccountRender()}
            </React.Fragment>
        );
    }
}

const mapStateToProps = (state, props) => {
    return {
        ...props,
        user_id: state.user_id,
        username: state.username,
        name: state.name,
        permission: state.permission,
        token: state.token,
        is_login: state.is_login,
    };
};

export default connect(mapStateToProps, null)(ProgrammingManage);